package com.mini8.backend.features.collect.repository;

import com.mini8.backend.database.blog.domain.entity.BlogPostCategoryEntity;
import com.mini8.backend.database.blog.domain.entity.BlogPostCategoryPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogPostCategoryRepository
    extends JpaRepository<BlogPostCategoryEntity, BlogPostCategoryPk> {}
