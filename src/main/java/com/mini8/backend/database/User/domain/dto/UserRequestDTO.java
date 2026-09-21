package com.mini8.backend.database.User.domain.dto;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Min;

public class UserRequestDTO {
   
    private Integer user_id;
  
    private String google_sub;

    private String name;
   
    private Integer career_years;

    private Integer profile_version;
  
    private String refresh_token;

    private String role;

    private LocalDate created_at,updated_at;
}
