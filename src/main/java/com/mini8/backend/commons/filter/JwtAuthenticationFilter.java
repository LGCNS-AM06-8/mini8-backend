package com.mini8.backend.commons.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mini8.backend.commons.token.JwtProvider;
import com.mini8.backend.commons.token.JwtProvider.JwtTokenException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** Authorization 헤더의 JWT를 검사하고 SecurityContext에 인증 정보를 저장한다. */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String ACCESS_TYPE = "access";
  private static final String USER_ROLE = "USER";
  private static final String ADMIN_ROLE = "ADMIN";

  private final JwtProvider jwtProvider;
  private final ObjectMapper objectMapper;

  public JwtAuthenticationFilter(JwtProvider jwtProvider, ObjectMapper objectMapper) {
    this.jwtProvider = jwtProvider;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // Authorization 헤더 조회
    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

    // 토큰이 없으면 그대로 통과하고, 인증 필요 여부는 SecurityConfig가 판단
    if (authorization == null) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      // Bearer 토큰 추출 및 JWT 검증
      String token = extractBearerToken(authorization);
      JwtProvider.ParsedToken parsedToken = jwtProvider.parse(token);

      // API 인증에는 Access Token만 허용
      validateAccessToken(parsedToken);

      // role을 Spring Security 권한 형식으로 변환
      SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + parsedToken.role());

      // userId를 principal로 인증 객체 생성
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(parsedToken.userId(), null, List.of(authority));

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      // 인증 정보를 SecurityContext에 저장
      SecurityContextHolder.getContext().setAuthentication(authentication);

      filterChain.doFilter(request, response);

    } catch (JwtTokenException exception) {
      // JWT 인증 실패 시 인증 정보 제거 후 401 반환
      SecurityContextHolder.clearContext();
      writeUnauthorized(response, exception.getCode());
    }
  }

  private String extractBearerToken(String authorization) {
    // Bearer 형식 검사
    if (!authorization.startsWith(BEARER_PREFIX)) {
      throw JwtTokenException.unauthorized();
    }

    String token = authorization.substring(BEARER_PREFIX.length()).trim();

    // Bearer 뒤에 실제 토큰이 없는 경우
    if (token.isEmpty()) {
      throw JwtTokenException.unauthorized();
    }

    return token;
  }

  private void validateAccessToken(JwtProvider.ParsedToken parsedToken) {
    // Refresh Token은 API 인증에 사용할 수 없음
    if (!ACCESS_TYPE.equals(parsedToken.type())) {
      throw JwtTokenException.unauthorized();
    }

    // USER / ADMIN 권한만 허용
    if (!USER_ROLE.equals(parsedToken.role()) && !ADMIN_ROLE.equals(parsedToken.role())) {
      throw JwtTokenException.unauthorized();
    }
  }

  private void writeUnauthorized(HttpServletResponse response, JwtTokenException.Code code)
      throws IOException {

    // 401 JSON 응답 생성
    ObjectNode body = objectMapper.createObjectNode();
    body.put("code", code.name());
    body.put("message", code.message());
    body.putNull("field");

    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    objectMapper.writeValue(response.getWriter(), body);
  }
}
