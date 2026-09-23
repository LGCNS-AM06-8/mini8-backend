package com.mini8.backend.features.user.service;

import com.mini8.backend.database.repository.UserRepository;
import com.mini8.backend.features.user.domain.dto.ProfileResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final UserRepository userRepository;

  // private final UserJobFieldRepository userJobFieldRepository;
  // private final UserSkillRepository userSkillRepository;

  public ProfileResponseDTO setProfile() {
    return null;
  }

  // TODO: UserJobFieldRepository와 UserSkillRepository 추가
  // TODO: 추가 후에는 임시로 넣어둔 return null; 을 지우고 주석 처리된 코드 주석 해제
  public ProfileResponseDTO getProfile(Long userId) {
    return null;
    // UserEntity user = userRepository.findById(userId)
    //   .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

    // List<String> jobFields = userJobFieldRepository.findAllByUser(user).stream()
    //   .map(UserJobFieldEntity::getJobField)
    //   .toList();

    // List<SkillResponseDTO> haveSkills = userSkillRepository.findAllByUserAndSkillType(user,
    // "HAVE").stream()
    //   .map(UserSkillEntity::getTechTag)
    //   .map(techTag -> new SkillResponseDTO(techTag.getTech_tag_id(), techTag.getName()))
    //   .toList();
    // List<SkillResponseDTO> wantSkills = userSkillRepository.findAllByUserAndSkillType(user,
    // "WANT").stream()
    //   .map(UserSkillEntity::getTechTag)
    //   .map(techTag -> new SkillResponseDTO(techTag.getTech_tag_id(), techTag.getName()))
    //   .toList();

    // return ProfileResponseDTO.builder()
    //   .userId(user.getUser_id())
    //   .name(user.getName())
    //   .jobFields(jobFields)
    //   .careerYears(user.getCareer_years())
    //   .haveSkills(haveSkills)
    //   .wantSkills(wantSkills)
    //   .profileVersion(user.getProfile_version())
    //   .build();
  }

  public ProfileResponseDTO editProfile() {
    return null;
  }
}
