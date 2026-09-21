package com.mini8.backend.features.post.repository;

import com.mini8.backend.features.post.domain.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Void> {}
