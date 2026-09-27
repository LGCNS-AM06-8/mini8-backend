package com.mini8.backend.features.user.service;

import com.mini8.backend.commons.exception.BusinessException;
import com.mini8.backend.commons.exception.ErrorCode;
import com.mini8.backend.commons.token.JwtProvider;
import com.mini8.backend.commons.token.JwtProvider.JwtTokenException;
import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.repository.UserRepository;
import com.mini8.backend.features.user.domain.dto.UserResponseDTO;
import com.mini8.backend.features.user.service.GoogleUserInfoClient.GoogleUserInfo;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private static final String REFRESH_TYPE = "refresh";

  private final GoogleUserInfoClient googleUserInfoClient;
  private final UserRepository userRepository;
  private final JwtProvider jwtProvider;

  @Transactional
  public LoginResult signIn(String googleAccessToken) {
    GoogleUserInfo info = googleUserInfoClient.getUserInfo(googleAccessToken);

    // 같은 Google 계정의 중복 가입을 막고, 첫 로그인일 때만 사용자를 만든다.
    UserEntity user = userRepository.findByGoogleSub(info.id()).orElseGet(() -> createUser(info));

    // Google 토큰은 사용자 확인에만 쓰고, 이후 인증에는 우리 JWT를 사용한다.
    String accessToken = jwtProvider.createAccessToken(user.getUser_id(), user.getRole());
    String refreshToken = jwtProvider.createRefreshToken(user.getUser_id());

    // 재발급·로그아웃 때 대조하도록 마지막으로 발급한 refresh 토큰을 저장한다.
    userRepository.updateRefreshToken(user.getUser_id(), refreshToken);

    UserResponseDTO response =
        UserResponseDTO.builder()
            .userId(user.getUser_id())
            .name(user.getName())
            .profileCompleted(user.getCareer_years() != null)
            .build();

    return new LoginResult(response, accessToken, refreshToken);
  }

  private UserEntity createUser(GoogleUserInfo info) {
    LocalDate now = LocalDate.now();

    // 신규 사용자는 프로필 미완료 상태로 생성한다.
    return userRepository.save(
        UserEntity.builder()
            .google_sub(info.id())
            .name(info.name())
            .career_years(null)
            .profile_version(0)
            .role("USER")
            .created_at(now)
            .updated_at(now)
            .build());
  }

  // 컨트롤러가 응답 바디와 두 토큰 헤더를 한 번에 조립하도록 묶은 내부 결과다.
  public record LoginResult(UserResponseDTO response, String accessToken, String refreshToken) {}

  // 로그아웃: 로그인한 사용자의 refresh 토큰을 폐기한다.
  @Transactional
  public void signOut(Long userId, String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_INPUT, "refreshToken");
    }

    // 저장된 토큰과 다르면 이미 쓸 수 없는 토큰이므로 지울 것이 없다.
    userRepository.clearRefreshToken(userId, refreshToken);
  }

  // 재발급: refresh 토큰이 서명·만료·종류가 맞고 DB에 저장된 값과 같을 때만 새 access 토큰을 준다.
  @Transactional(readOnly = true)
  public String refreshToken(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    JwtProvider.ParsedToken parsedToken;
    try {
      parsedToken = jwtProvider.parse(refreshToken);
    } catch (JwtTokenException exception) {
      // 만료·서명 오류 모두 재발급 불가로 처리한다.
      throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    // access 토큰으로는 재발급할 수 없다.
    if (!REFRESH_TYPE.equals(parsedToken.type())) {
      throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    // 로그아웃했거나 다시 로그인해 바뀐 토큰은 폐기된 것으로 본다.
    UserEntity user =
        userRepository
            .findById(parsedToken.userId())
            .filter(found -> refreshToken.equals(found.getRefresh_token()))
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN));

    return jwtProvider.createAccessToken(user.getUser_id(), user.getRole());
  }
}
