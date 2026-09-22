package com.mini8.backend.database.repository;

import com.mini8.backend.database.User.domain.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  // 엔티티 필드 이름에 밑줄이 있어 findByGoogleSub 으로는 못 만든다. JPQL 로 직접 적는다.
  @Query("select u from UserEntity u where u.google_sub = :googleSub")
  Optional<UserEntity> findByGoogleSub(@Param("googleSub") String googleSub);
}
