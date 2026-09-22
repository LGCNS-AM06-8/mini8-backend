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

  private int userId;

  private List<String> jobFields;

  private int careerYears;

  private List<Integer> haveSkillIds;

  private List<Integer> wantSkillIds;

  private int profileVersion;
}
