package com.mini8.backend.database.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.mini8.backend.database.Tech.domain.entity.TechTagEntity;
import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.User.domain.entity.UserJobFieldEntity;
import com.mini8.backend.database.User.domain.entity.UserSkillEntity;
import com.mini8.backend.database.User.domain.entity.UserSkillPk;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

/** 프로필 조회(GET /api/profile)가 쓰는 두 조회가 실제로 도는지. */
@DataJpaTest
@ActiveProfiles("test")
class ProfileRepositoryTest {

  @Autowired UserRepository users;
  @Autowired TechTagRepository techTags;
  @Autowired UserJobFieldRepository jobFields;
  @Autowired UserSkillRepository skills;

  @Test
  void 희망_직무와_보유_관심_기술을_사용자별로_읽는다() {
    UserEntity user =
        users.save(
            UserEntity.builder()
                .google_sub("test-sub")
                .name("테스트")
                .created_at(LocalDate.now())
                .updated_at(LocalDate.now())
                .build());
    TechTagEntity kafka = techTags.save(TechTagEntity.builder().name("Kafka").build());
    TechTagEntity redis = techTags.save(TechTagEntity.builder().name("Redis").build());

    jobFields.save(UserJobFieldEntity.builder().user(user).jobField("Backend").build());
    jobFields.save(UserJobFieldEntity.builder().user(user).jobField("Data").build());
    skills.save(skill(user, kafka, "HAVE"));
    skills.save(skill(user, redis, "WANT"));

    assertThat(jobFields.findAllByUser(user))
        .extracting(UserJobFieldEntity::getJobField)
        .containsExactlyInAnyOrder("Backend", "Data");
    assertThat(skills.findAllByUserAndSkillType(user, "HAVE"))
        .extracting(s -> s.getTechTag().getName())
        .containsExactly("Kafka");
    assertThat(skills.findAllByUserAndSkillType(user, "WANT"))
        .extracting(s -> s.getTechTag().getName())
        .containsExactly("Redis");
  }

  private UserSkillEntity skill(UserEntity user, TechTagEntity tag, String type) {
    return UserSkillEntity.builder()
        .id(new UserSkillPk(user.getUser_id(), tag.getTech_tag_id()))
        .user(user)
        .techTag(tag)
        .skill_type(type)
        .build();
  }
}
