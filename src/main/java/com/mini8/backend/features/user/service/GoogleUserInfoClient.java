package com.mini8.backend.features.user.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mini8.backend.commons.exception.BusinessException;
import com.mini8.backend.commons.exception.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GoogleUserInfoClient {

  // Google API 기본 주소
  private static final String GOOGLE_API_BASE_URL = "https://www.googleapis.com";

  // 사용자 정보 조회 API
  private static final String USER_INFO_PATH = "/oauth2/v1/userinfo";

  private final RestClient restClient;

  public GoogleUserInfoClient(RestClient.Builder restClientBuilder) {
    this.restClient = restClientBuilder.baseUrl(GOOGLE_API_BASE_URL).build();
  }

  // Google Access Token으로 사용자 정보 조회
  public GoogleUserInfo getUserInfo(String googleAccessToken) {
    return restClient
        .get()
        .uri(USER_INFO_PATH)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + googleAccessToken)
        .retrieve()
        // 잘못된 Google Access Token 처리
        .onStatus(
            HttpStatusCode::isError,
            (request, response) -> {
              throw new BusinessException(ErrorCode.INVALID_GOOGLE_TOKEN);
            })
        .body(GoogleUserInfo.class);
  }

  // Google의 id는 app_user.google_sub와 연결하는 변경되지 않는 사용자 식별자다.
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record GoogleUserInfo(String id, String email, String name) {}
}
