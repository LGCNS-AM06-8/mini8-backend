package com.mini8.backend.features.collect.domain;

/**
 * 한 기업을 어디서 어떻게 받는지. 표가 생기면 company 행에서 읽어 만든다.
 *
 * @param company 기업 이름. 수집 결과에 그대로 담는다
 * @param type 받는 방식
 * @param url 목록 주소(피드 · JSON · 사이트맵)
 */
public record BlogSource(String company, SourceType type, String url) {}
