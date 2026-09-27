package com.mini8.backend.features.user.ctrl;

import com.mini8.backend.features.user.domain.dto.LogoutRequestDTO;
import com.mini8.backend.features.user.domain.dto.UserRequestDTO;
import com.mini8.backend.features.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  // signIn: OAuth 로그인
  @Operation(summary = "OAuth 로그인", description = "구글 계정을 사용해 로그인")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "로그인 성공"),
    @ApiResponse(responseCode = "401", description = "로그인 실패(access token으로 Google userinfo 조회 실패)")
  })
  @PostMapping("/google")
  public ResponseEntity<?> signIn(@RequestBody UserRequestDTO request) {
    UserService.LoginResult result = userService.signIn(request.getGoogleAccessToken());

    // 우리 서비스에서 사용할 JWT는 바디가 아닌 응답 헤더로 전달한다.
    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + result.accessToken())
        .header("Refresh-Token", result.refreshToken())
        .body(result.response());
  }

  // signOut: OAuth 로그아웃
  @Operation(summary = "OAuth 로그아웃", description = "로그아웃 실행")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
    @ApiResponse(responseCode = "400", description = "로그아웃 실패(refreshToken 누락)"),
    @ApiResponse(responseCode = "401", description = "로그아웃 실패(토큰 유효성 확인)")
  })
  @PostMapping("/logout")
  public ResponseEntity<?> signOut(
      @AuthenticationPrincipal Long userId,
      @RequestBody(required = false) LogoutRequestDTO request) {
    // 바디가 아예 없어도 500 이 아니라 입력 오류로 돌려주도록 서비스에서 검사한다.
    userService.signOut(userId, request == null ? null : request.getRefreshToken());
    return ResponseEntity.noContent().build();
  }

  // refreshToken: access token 재발급
  @Operation(summary = "access token 재발급", description = "access token이 만료되면 FE에서 자동으로 호출함")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "token 재발급 성공"),
    @ApiResponse(responseCode = "403", description = "token 재발급 실패(refresh token 유효성 확인)")
  })
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken(
      @RequestHeader(value = "Refresh-Token", required = false) String refreshToken) {
    String accessToken = userService.refreshToken(refreshToken);

    // 로그인과 같이 새 access 토큰은 응답 헤더로만 준다. 바디는 비어 있다.
    return ResponseEntity.status(HttpStatus.OK)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
        .build();
  }
}
