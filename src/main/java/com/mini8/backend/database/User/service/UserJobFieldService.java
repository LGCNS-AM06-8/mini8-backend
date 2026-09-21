package com.mini8.backend.database.User.service;

import org.springframework.stereotype.Service;

import com.mini8.backend.database.User.domain.dto.UserJobFieldRequestDTO;
import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.User.domain.entity.UserJobFieldEntity;
import com.mini8.backend.database.User.repository.UserJobFieldRepository;
import com.mini8.backend.database.User.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserJobFieldService {

    private final UserJobFieldRepository userJobFieldRepository;
    private final UserRepository userRepository;

    @Transactional
    public void save(UserJobFieldRequestDTO request) {

        // 1. userId로 사용자 조회
        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. UserJobFieldEntity 생성
        UserJobFieldEntity userJobField = UserJobFieldEntity.builder()
                .user(user)
                .jobField(request.getJobField())
                .build();

        // 3. 저장
        userJobFieldRepository.save(userJobField);
    }
}