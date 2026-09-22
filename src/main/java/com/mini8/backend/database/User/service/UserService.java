package com.mini8.backend.database.User.service;

import org.springframework.stereotype.Service;

import com.mini8.backend.database.User.domain.dto.UserRequestDTO;
import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.User.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void save(UserRequestDTO request) {

        UserEntity user = UserEntity.builder()
                .google_sub(request.getGoogle_sub())
                .name(request.getName())
                // 구글 로그인시 이름 여기방향으로 추가 
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