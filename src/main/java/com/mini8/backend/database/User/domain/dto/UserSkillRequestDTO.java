package com.mini8.backend.database.User.domain.dto;

import com.mini8.backend.database.Tech.domain.entity.TechTagEntity;
import com.mini8.backend.database.User.domain.entity.UserEntity;

// 마이페이지에서 지정하면 Db로 올라가는 파일 
public class UserSkillRequestDTO {
    private UserEntity user;
    private TechTagEntity techTag;
}
