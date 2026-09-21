package com.mini8.backend.features.tech.repository;

import com.mini8.backend.features.tech.domain.entity.TechEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechRepository extends JpaRepository<TechEntity, Void> {}
