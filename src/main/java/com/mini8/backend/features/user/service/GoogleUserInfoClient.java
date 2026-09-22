package com.mini8.backend.features.user.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
            HttpStatusCode::is4xxClientError,
            (request, response) -> {
              throw new InvalidGoogleTokenException();
            })
        .body(GoogleUserInfo.class);
  }

  // Google 응답에서 필요한 값만 사용
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record GoogleUserInfo(String id, String email, String name) {}

  // 공통 예외 적용 전 임시 예외
  public static class InvalidGoogleTokenException extends RuntimeException {

    public static final String CODE = "INVALID_GOOGLE_TOKEN";

    public InvalidGoogleTokenException() {
      super("유효하지 않은 Google Access Token입니다.");
    }

    public String getCode() {
      return CODE;
    }
  }
}
