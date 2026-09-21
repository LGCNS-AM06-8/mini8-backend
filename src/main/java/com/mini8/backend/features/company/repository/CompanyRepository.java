package com.mini8.backend.features.company.repository;

import com.mini8.backend.features.company.domain.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Void> {}
