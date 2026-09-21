package com.mini8.backend.features.bookmark.repository;

import com.mini8.backend.features.bookmark.domain.entity.BookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Void> {}
