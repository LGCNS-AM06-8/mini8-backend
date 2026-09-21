package com.mini8.backend.database.User.domain.dto;


import java.time.LocalDate;
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


public class UserRequestDTO {
   
   
  
    private String google_sub;

    private String name;
   
    private Integer career_years;

    private Integer profile_version;
  
    private String refresh_token;

    private String role;

    private LocalDate created_at,updated_at;
}
