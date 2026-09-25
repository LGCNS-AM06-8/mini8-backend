package com.mini8.backend.database.repository;

import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.User.domain.entity.UserJobFieldEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJobFieldRepository extends JpaRepository<UserJobFieldEntity, Integer> {
  List<UserJobFieldEntity> findAllByUser(UserEntity user);
}
