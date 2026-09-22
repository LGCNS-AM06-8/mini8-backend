package com.mini8.backend.features.collect.repository;

import com.mini8.backend.database.blog.domain.entity.BlogPostEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogPostRepository extends JpaRepository<BlogPostEntity, Long> {
  Optional<BlogPostEntity> findByUrl(String url);

  boolean existsByUrl(String url);
}
