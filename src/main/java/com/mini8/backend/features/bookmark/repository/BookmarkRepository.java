package com.mini8.backend.features.bookmark.repository;

import com.mini8.backend.database.bookmark.domain.entity.BookmarkEntity;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long> {

    @Query("""
        SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
        FROM BookmarkEntity b
        WHERE b.user.user_id = :userId
          AND b.blogPost.blog_post_id = :postId
    """)
    boolean existsBookmark(
            @Param("userId") Long userId,
            @Param("postId") Long postId
    );

    @Query("""
        SELECT b
        FROM BookmarkEntity b
        WHERE b.user.user_id = :userId
        ORDER BY b.created_at DESC
    """)
    List<BookmarkEntity> findBookmarksByUser(
            @Param("userId") Long userId
    );


     // 자우는 코드
    @Query("""
    SELECT b
    FROM BookmarkEntity b
    WHERE b.user.user_id = :userId
      AND b.blogPost.blog_post_id = :postId
        """)
        Optional<BookmarkEntity> findBookmark(
                @Param("userId") Long userId,
                @Param("postId") Long postId
        );
}
