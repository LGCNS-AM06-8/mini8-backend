package com.mini8.backend.features.tech.domain.dto;

import java.util.List;

/** GET /api/tech-tags 응답. 기술 칩 목록. */
public record TechResponseDTO(List<TechTagDTO> techTags) {

  public record TechTagDTO(Long techTagId, String name, String field) {}
}
