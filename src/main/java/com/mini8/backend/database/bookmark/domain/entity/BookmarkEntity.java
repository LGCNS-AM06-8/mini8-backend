package com.mini8.backend.database.bookmark.domain.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookmark_id;
    private Long user_id;
    private Long blog_post_id;
    @Column(nullable = false)
    private LocalDateTime created_at;
}
