package com.mini8.backend.features.user.service;

import com.mini8.backend.commons.exception.BusinessException;
import com.mini8.backend.commons.exception.ErrorCode;
import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.User.domain.entity.UserJobFieldEntity;
import com.mini8.backend.database.User.domain.entity.UserSkillEntity;
import com.mini8.backend.database.repository.UserJobFieldRepository;
import com.mini8.backend.database.repository.UserRepository;
import com.mini8.backend.database.repository.UserSkillRepository;
import com.mini8.backend.features.user.domain.dto.ProfileResponseDTO;
import com.mini8.backend.features.user.domain.dto.ProfileResponseDTO.SkillResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final UserRepository userRepository;

  private final UserJobFieldRepository userJobFieldRepository;
  private final UserSkillRepository userSkillRepository;

  public ProfileResponseDTO setProfile() {
    return null;
  }

  public ProfileResponseDTO getProfile(Long userId) {
    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

    List<String> jobFields =
        userJobFieldRepository.findAllByUser(user).stream()
            .map(UserJobFieldEntity::getJobField)
            .toList();

    List<SkillResponseDTO> haveSkills =
        userSkillRepository.findAllByUserAndSkillType(user, "HAVE").stream()
            .map(UserSkillEntity::getTechTag)
            .map(techTag -> new SkillResponseDTO(techTag.getTech_tag_id(), techTag.getName()))
            .toList();
    List<SkillResponseDTO> wantSkills =
        userSkillRepository.findAllByUserAndSkillType(user, "WANT").stream()
            .map(UserSkillEntity::getTechTag)
            .map(techTag -> new SkillResponseDTO(techTag.getTech_tag_id(), techTag.getName()))
            .toList();

    return ProfileResponseDTO.builder()
        .userId(user.getUser_id())
        .name(user.getName())
        .jobFields(jobFields)
        .careerYears(user.getCareer_years())
        .haveSkills(haveSkills)
        .wantSkills(wantSkills)
        .profileVersion(user.getProfile_version())
        .build();
  }

  public ProfileResponseDTO editProfile() {
    return null;
  }
}
