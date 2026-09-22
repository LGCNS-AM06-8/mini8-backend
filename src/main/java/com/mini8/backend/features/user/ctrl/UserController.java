package com.mini8.backend.features.user.ctrl;

import com.mini8.backend.features.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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
  public ResponseEntity<?> signIn() {
    return null;
  }

  // signOut: OAuth 로그아웃
  @Operation(summary = "OAuth 로그아웃", description = "로그아웃 실행")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
    @ApiResponse(responseCode = "401", description = "로그아웃 실패(토큰 유효성 확인)")
  })
  @PostMapping("/logout")
  public ResponseEntity<?> signOut() {
    return null;
  }

  // refreshToken: access token 재발급
  @Operation(summary = "access token 재발급", description = "access token이 만료되면 FE에서 자동으로 호출함")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "token 재발급 성공"),
    @ApiResponse(responseCode = "403", description = "token 재발급 실패(refresh token 유효성 확인)")
  })
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken() {
    return null;
  }
}
