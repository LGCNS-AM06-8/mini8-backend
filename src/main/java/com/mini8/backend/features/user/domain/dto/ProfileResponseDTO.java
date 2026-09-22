package com.mini8.backend.features.user.domain.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {

  private String name;

  private int userId;

  private List<String> jobFields;

  private Integer careerYears;

  private List<SkillResponseDTO> haveSkillIds;

  private List<SkillResponseDTO> wantSkillIds;

  private int profileVersion;

  public record SkillResponseDTO(int techTagId, String name) {}
}
