package com.mini8.backend.features.bookmark.repository;

import com.mini8.backend.database.bookmark.domain.entity.BookmarkEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long> {

    // 중복 막는 코드
    boolean existsByUser_User_idAndBlogPost_Blog_post_id(
            Long userId,
            Long postId
    );
    List<BookmarkEntity> findByUser_User_idOrderByCreated_atDesc(Long userId);
    
}
