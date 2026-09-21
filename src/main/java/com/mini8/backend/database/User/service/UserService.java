package com.mini8.backend.database.User.service;

import org.springframework.stereotype.Service;

import com.mini8.backend.database.TechBlog.domain.dto.TechBlogRequestDTO;
import com.mini8.backend.database.TechBlog.domain.entity.TechBlogEntity;
import com.mini8.backend.database.User.domain.dto.UserRequestDTO;
import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.User.domain.entity.UserJobFieldEntity;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    @Transactional
    public void save(UserRequestDTO request) {

        // 1. 게시글 저장
        UserJobFieldEntity user = UserEntity.builder()
        .google_sub(request.getGoogle_sub())
        .name(request.getName())
        .career_years(request.getCareer_years())
        .profile_version(request.getProfile_version())
        .refresh_token(request.getRefresh_token())
        .role(request.getRole())
        .created_at(request.getCreated_at())
        .updated_at(request.getUpdated_at())
        .build();

    UserEntity savedUser = userRepository.save(user);
    }
}
