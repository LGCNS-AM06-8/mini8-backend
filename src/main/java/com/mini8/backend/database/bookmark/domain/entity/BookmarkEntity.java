package com.mini8.backend.database.bookmark.domain.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "bookmark", uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_ai_guide",
            columnNames = {
                "user_id",
                "blog_post_id"
            }
        )
    }
)
public class BookmarkEntity {
    private Long bookmark_id;
    private Long user_id;
    private Long blog_post_id;
    @Column(nullable = false)
    private LocalDateTime created_at;
}
