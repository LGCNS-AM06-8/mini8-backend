package com.mini8.backend.commons.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 1일차용 최소 보안 설정. Swagger 와 모든 경로를 열어 두고 CORS 만 건다.
 *
 * <p>S1(JWT 필터)이 들어오면 박준우님이 anyRequest() 를 authenticated() 로 바꾸고 필터를 등록한다. 토큰 없이 부를 수 있는 경로는 노션 명세
 * 「인증 불필요」 행 = /api/auth/**, /api/tech-tags, GET /api/companies/{id}.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Value("${app.cors-origins}")
  private List<String> corsOrigins;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .cors(Customizer.withDefaults())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .httpBasic(b -> b.disable())
        .formLogin(f -> f.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**")
                    .permitAll()
                    .requestMatchers("/api/auth/**", "/api/tech-tags")
                    .permitAll()
                    // S1 전까지 전부 허용. S1 에서 authenticated() 로 조인다
                    .anyRequest()
                    .permitAll());
    return http.build();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(corsOrigins);
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    // 토큰이 응답 헤더로 나가므로 이 둘을 노출해야 브라우저 JS 가 읽는다
    config.setExposedHeaders(List.of("Authorization", "Refresh-Token"));
    config.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
