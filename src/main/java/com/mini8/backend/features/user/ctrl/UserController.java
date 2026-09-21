package com.mini8.backend.features.user.ctrl;

import com.mini8.backend.features.user.service.UserService;
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
  @PostMapping("/google")
  public ResponseEntity<?> signIn() {
    return null;
  }

  // signOut: OAuth 로그아웃
  @PostMapping("/logout")
  public ResponseEntity<?> signOut() {
    return null;
  }

  // refreshToken
  @PostMapping("/refresh")
  public ResponseEntity<?> refreshToken() {
    return null;
  }
}
