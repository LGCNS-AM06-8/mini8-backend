package com.mini8.backend.database.User.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.mini8.backend.database.User.domain.entity.UserJobFieldEntity;

public interface UserJobFieldRepository extends JpaRepository<UserJobFieldEntity, Long>{
    
}
