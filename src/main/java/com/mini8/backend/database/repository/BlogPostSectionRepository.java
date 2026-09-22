package com.mini8.backend.database.repository;

import com.mini8.backend.database.blog.domain.entity.BlogPostSectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogPostSectionRepository extends JpaRepository<BlogPostSectionEntity, Long> {}
