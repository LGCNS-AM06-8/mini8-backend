package com.mini8.backend.features.bookmark.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mini8.backend.database.bookmark.domain.entity.BookmarkEntity;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity,Long>{
    
}
