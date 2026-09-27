package com.mini8.backend.features.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 토큰이 로그에 찍히지 않도록 @ToString 은 붙이지 않는다
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequestDTO {

  // 폐기할 refresh 토큰 (로그인 때 Refresh-Token 헤더로 받은 값)
  private String refreshToken;
}
