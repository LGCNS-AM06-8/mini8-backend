package com.mini8.backend.features.collect.ctrl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mini8.backend.commons.token.JwtProvider;
import com.mini8.backend.features.collect.service.CollectJobService;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** 웹 수집 입구의 권한. 명세대로 토큰 없음 401 · USER 403 · ADMIN 202 다. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminCollectControllerTest {

  @Autowired MockMvc mvc;
  @Autowired JwtProvider jwtProvider;

  @MockitoBean CollectJobService jobService;

  @Test
  void 토큰이_없으면_401() throws Exception {
    mvc.perform(post("/api/admin/collect"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
  }

  @Test
  void USER_는_403() throws Exception {
    String token = jwtProvider.createAccessToken(2L, "USER");

    mvc.perform(post("/api/admin/collect").header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.code").value("FORBIDDEN"));
  }

  @Test
  void ADMIN_은_바로_202_와_작업_번호를_받는다() throws Exception {
    when(jobService.start(any()))
        .thenReturn(
            new CollectJobService.Job(
                "collect-20260927-120000", OffsetDateTime.parse("2026-09-27T12:00:00+09:00")));
    String token = jwtProvider.createAccessToken(1L, "ADMIN");

    mvc.perform(
            post("/api/admin/collect")
                .header("Authorization", "Bearer " + token)
                .contentType("application/json")
                .content("{\"companyId\": 1}"))
        .andExpect(status().isAccepted())
        .andExpect(jsonPath("$.jobId").value("collect-20260927-120000"))
        .andExpect(jsonPath("$.startedAt").value("2026-09-27T12:00:00+09:00"));
  }
}
