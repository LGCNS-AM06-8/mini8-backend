package com.mini8.backend.database.repository;

import com.mini8.backend.database.blog.domain.entity.BlogPostTagEntity;
import com.mini8.backend.database.blog.domain.entity.BlogPostTagPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogPostTagRepository extends JpaRepository<BlogPostTagEntity, BlogPostTagPk> {}
