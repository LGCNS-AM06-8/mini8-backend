package com.mini8.backend.features.user.service;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GoogleUserInfoClient {

  private static final String TOKEN_INFO_URL =
      "https://oauth2.googleapis.com/tokeninfo?access_token={accessToken}";
  private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";

  private final RestClient restClient;
  private final String googleClientId;

  public GoogleUserInfoClient(
      RestClient.Builder restClientBuilder, @Value("${google.client-id}") String googleClientId) {
    this.restClient =
        restClientBuilder
            .defaultStatusHandler(
                HttpStatusCode::is4xxClientError,
                (request, response) -> {
                  throw new InvalidGoogleTokenException();
                })
            .build();
    this.googleClientId = googleClientId;
  }

  public GoogleUserInfo getUserInfo(String googleAccessToken) {
    if (googleAccessToken == null || googleAccessToken.isBlank()) {
      throw new InvalidGoogleTokenException();
    }

    GoogleTokenInfo tokenInfo =
        restClient
            .get()
            .uri(TOKEN_INFO_URL, googleAccessToken)
            .retrieve()
            .body(GoogleTokenInfo.class);

    if (googleClientId == null
        || googleClientId.isBlank()
        || tokenInfo == null
        || !googleClientId.equals(tokenInfo.clientId())) {
      throw new InvalidGoogleTokenException();
    }

    return restClient
        .get()
        .uri(USER_INFO_URL)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + googleAccessToken)
        .retrieve()
        .body(GoogleUserInfo.class);
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private record GoogleTokenInfo(@JsonAlias({"aud", "audience", "issued_to"}) String clientId) {}

  @JsonIgnoreProperties(ignoreUnknown = true)
  public record GoogleUserInfo(String id, String email, String name) {}

  /** 공통 BusinessException과 ErrorCode가 들어오기 전까지 Google 인증 실패만 구분한다. */
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
