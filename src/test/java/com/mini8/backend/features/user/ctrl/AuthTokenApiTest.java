package com.mini8.backend.features.user.ctrl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mini8.backend.commons.token.JwtProvider;
import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.repository.UserRepository;
import com.mini8.backend.features.user.service.GoogleUserInfoClient;
import com.mini8.backend.features.user.service.GoogleUserInfoClient.GoogleUserInfo;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** 토큰 재발급(POST /api/auth/refresh)과 로그아웃(POST /api/auth/logout)이 API 명세대로 도는지. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthTokenApiTest {

  private static final String REFRESH_HEADER = "Refresh-Token";

  @Autowired MockMvc mockMvc;
  @Autowired UserRepository users;
  @Autowired JwtProvider jwtProvider;

  // 실제 구글은 부르지 않는다
  @MockitoBean GoogleUserInfoClient googleUserInfoClient;

  @Value("${jwt.secret}")
  String secret;

  private UserEntity user;

  @BeforeEach
  void setUp() {
    // 다른 테스트와 같은 H2 를 쓰므로 사용자마다 google_sub 을 다르게 만든다
    user =
        users.save(
            UserEntity.builder()
                .google_sub("sub-" + UUID.randomUUID())
                .name("테스트")
                .created_at(LocalDate.now())
                .updated_at(LocalDate.now())
                .build());
  }

  @Test
  void 로그인하면_발급한_refresh_토큰을_저장한다() throws Exception {
    String sub = "sub-" + UUID.randomUUID();
    given(googleUserInfoClient.getUserInfo("google-token"))
        .willReturn(new GoogleUserInfo(sub, "a@b.c", "테스트"));

    MvcResult result =
        mockMvc
            .perform(
                post("/api/auth/google")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"googleAccessToken\":\"google-token\"}"))
            .andExpect(status().isOk())
            .andReturn();

    String issued = result.getResponse().getHeader(REFRESH_HEADER);
    assertThat(issued).isNotBlank();
    assertThat(users.findByGoogleSub(sub).orElseThrow().getRefresh_token()).isEqualTo(issued);
  }

  @Test
  void 저장된_refresh_토큰이면_새_access_토큰을_헤더로_준다() throws Exception {
    String refreshToken = loginAs(user);

    MvcResult result =
        mockMvc
            .perform(post("/api/auth/refresh").header(REFRESH_HEADER, refreshToken))
            .andExpect(status().isOk())
            .andExpect(content().string(""))
            .andReturn();

    String authorization = result.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    assertThat(authorization).startsWith("Bearer ");
    JwtProvider.ParsedToken parsed = jwtProvider.parse(authorization.substring("Bearer ".length()));
    assertThat(parsed.userId()).isEqualTo(user.getUser_id());
    assertThat(parsed.type()).isEqualTo("access");
  }

  @Test
  void 만료된_access_토큰이_같이_와도_재발급은_된다() throws Exception {
    String refreshToken = loginAs(user);
    String expiredAccess =
        new JwtProvider(secret, -1000, 604800000).createAccessToken(user.getUser_id(), "USER");

    mockMvc
        .perform(
            post("/api/auth/refresh")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredAccess)
                .header(REFRESH_HEADER, refreshToken))
        .andExpect(status().isOk())
        .andExpect(header().string(HttpHeaders.AUTHORIZATION, startsWith("Bearer ")));
  }

  @Test
  void refresh_토큰이_없으면_403() throws Exception {
    expectInvalidRefresh(null);
  }

  @Test
  void 형식이_깨진_refresh_토큰이면_403() throws Exception {
    loginAs(user);
    expectInvalidRefresh("not-a-jwt");
  }

  @Test
  void 만료된_refresh_토큰이면_403() throws Exception {
    // 만료된 토큰을 DB 에도 저장해, 거절 이유가 만료뿐이게 한다
    String expired = new JwtProvider(secret, 1800000, -1000).createRefreshToken(user.getUser_id());
    users.updateRefreshToken(user.getUser_id(), expired);

    expectInvalidRefresh(expired);
  }

  @Test
  void access_토큰을_refresh_자리에_넣으면_403() throws Exception {
    loginAs(user);
    expectInvalidRefresh(jwtProvider.createAccessToken(user.getUser_id(), "USER"));
  }

  @Test
  void 저장된_값과_다른_refresh_토큰이면_403() throws Exception {
    users.updateRefreshToken(user.getUser_id(), "다른-토큰");
    expectInvalidRefresh(jwtProvider.createRefreshToken(user.getUser_id()));
  }

  @Test
  void 로그아웃하면_refresh_토큰을_지우고_이후_재발급은_403() throws Exception {
    String refreshToken = loginAs(user);

    mockMvc
        .perform(
            post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessOf(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
        .andExpect(status().isNoContent())
        .andExpect(content().string(""));

    assertThat(users.findById(user.getUser_id()).orElseThrow().getRefresh_token()).isNull();
    expectInvalidRefresh(refreshToken);
  }

  @Test
  void 저장된_값과_다른_토큰으로_로그아웃하면_저장된_토큰은_남는다() throws Exception {
    String refreshToken = loginAs(user);

    mockMvc
        .perform(
            post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessOf(user))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"다른-토큰\"}"))
        .andExpect(status().isNoContent());

    assertThat(users.findById(user.getUser_id()).orElseThrow().getRefresh_token())
        .isEqualTo(refreshToken);
  }

  @Test
  void access_토큰_없이_로그아웃하면_401() throws Exception {
    String refreshToken = loginAs(user);

    mockMvc
        .perform(
            post("/api/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));

    // 인증에 실패했으니 토큰은 그대로다
    assertThat(users.findById(user.getUser_id()).orElseThrow().getRefresh_token())
        .isEqualTo(refreshToken);
  }

  @Test
  void refresh_토큰으로_로그아웃_인증을_하면_401() throws Exception {
    String refreshToken = loginAs(user);

    mockMvc
        .perform(
            post("/api/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void 로그아웃_바디가_없으면_400() throws Exception {
    loginAs(user);

    mockMvc
        .perform(
            post("/api/auth/logout").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessOf(user)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("INVALID_INPUT"))
        .andExpect(jsonPath("$.field").value("refreshToken"));
  }

  // 로그인과 같은 상태를 만든다: refresh 토큰을 발급해 DB 에 저장
  private String loginAs(UserEntity target) {
    String refreshToken = jwtProvider.createRefreshToken(target.getUser_id());
    users.updateRefreshToken(target.getUser_id(), refreshToken);
    return refreshToken;
  }

  private String accessOf(UserEntity target) {
    return jwtProvider.createAccessToken(target.getUser_id(), "USER");
  }

  private void expectInvalidRefresh(String refreshToken) throws Exception {
    var request = post("/api/auth/refresh");
    if (refreshToken != null) {
      request.header(REFRESH_HEADER, refreshToken);
    }
    mockMvc
        .perform(request)
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("INVALID_REFRESH_TOKEN"))
        .andExpect(header().doesNotExist(HttpHeaders.AUTHORIZATION));
  }
}
