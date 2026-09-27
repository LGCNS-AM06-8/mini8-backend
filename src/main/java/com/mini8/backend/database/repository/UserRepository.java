package com.mini8.backend.database.repository;

import com.mini8.backend.database.User.domain.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

  // 엔티티 필드 이름에 밑줄이 있어 findByGoogleSub 으로는 못 만든다. JPQL 로 직접 적는다.
  @Query("select u from UserEntity u where u.google_sub = :googleSub")
  Optional<UserEntity> findByGoogleSub(@Param("googleSub") String googleSub);

  // 로그인 때 새로 발급한 refresh 토큰으로 덮어쓴다. 엔티티에 setter 가 없어 JPQL 로 갱신한다.
  @Transactional
  @Modifying(clearAutomatically = true)
  @Query("update UserEntity u set u.refresh_token = :refreshToken where u.user_id = :userId")
  int updateRefreshToken(@Param("userId") Long userId, @Param("refreshToken") String refreshToken);

  // 로그아웃: 저장된 refresh 토큰이 요청한 토큰과 같을 때만 지운다.
  @Transactional
  @Modifying(clearAutomatically = true)
  @Query(
      "update UserEntity u set u.refresh_token = null"
          + " where u.user_id = :userId and u.refresh_token = :refreshToken")
  int clearRefreshToken(@Param("userId") Long userId, @Param("refreshToken") String refreshToken);
}
