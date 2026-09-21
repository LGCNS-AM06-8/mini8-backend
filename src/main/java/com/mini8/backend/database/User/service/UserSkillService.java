package com.mini8.backend.database.User.service;
import org.springframework.stereotype.Service;


import com.mini8.backend.database.User.domain.dto.UserSkillRequestDTO;

import com.mini8.backend.database.User.domain.entity.UserSkillEntity;

import com.mini8.backend.database.User.repository.UserSkillRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserSkillService {
    private final UserSkillRepository userSkillRepository;

    @Transactional
    public void save(UserSkillRequestDTO request) {

        UserSkillEntity userSkill = UserSkillEntity.builder()
                
                .build();

        UserSkillEntity savedUserSkill = userSkillRepository.save(userSkill);
    }
}
