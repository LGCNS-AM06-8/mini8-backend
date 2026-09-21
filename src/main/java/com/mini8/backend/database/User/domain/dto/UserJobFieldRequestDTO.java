package com.mini8.backend.database.User.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserJobFieldRequestDTO {

    private Long  userId;

    private String jobField;

}
