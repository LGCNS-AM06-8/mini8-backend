package com.mini8.backend.database.bookmark.domain.entity;

import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.blog.domain.entity.BlogPostEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "bookmark",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_bookmark",
          columnNames = {"user_id", "blog_post_id"})
    })
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long bookmark_id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "blog_post_id", nullable = false)
  private BlogPostEntity blogPost;

  @Column(nullable = false)
  private LocalDateTime created_at;
}
