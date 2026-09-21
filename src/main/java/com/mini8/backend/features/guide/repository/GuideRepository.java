package com.mini8.backend.features.guide.repository;

import com.mini8.backend.features.guide.domain.entity.GuideEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideRepository extends JpaRepository<GuideEntity, Void> {}
