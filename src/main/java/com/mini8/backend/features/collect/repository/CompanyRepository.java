package com.mini8.backend.features.collect.repository;

import com.mini8.backend.database.company.domain.entity.CompanyEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
  Optional<CompanyEntity> findByName(String name);
}
