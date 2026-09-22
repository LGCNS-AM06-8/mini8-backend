package com.mini8.backend.features.collect.repository;

import com.mini8.backend.database.Tech.domain.entity.TechTagEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechTagRepository extends JpaRepository<TechTagEntity, Long> {
  Optional<TechTagEntity> findByName(String name);
}
