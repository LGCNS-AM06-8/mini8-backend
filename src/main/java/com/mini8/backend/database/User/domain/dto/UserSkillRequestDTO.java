package com.mini8.backend.database.User.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSkillRequestDTO {

    private Long  userId;

    private Long  techTagId;

    private String skillType;
}