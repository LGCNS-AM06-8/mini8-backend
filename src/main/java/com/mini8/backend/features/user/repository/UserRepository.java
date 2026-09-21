package com.mini8.backend.features.user.repository;

import com.mini8.backend.features.user.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Void> {}
