package com.mini8.backend.database.ai_guide.domain.entity;

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
    name = "ai_guide",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_ai_guide",
          columnNames = {"user_id", "blog_post_id", "profile_version", "prompt_version"})
    })
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiGuideEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long ai_guide_id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "blog_post_id", nullable = false)
  private BlogPostEntity blogPost;

  @Column(nullable = false)
  private Integer profile_version;

  @Column(nullable = false, length = 20)
  private String prompt_version;

  @Column(length = 100, nullable = true)
  private String focus_sections;

  @Column(length = 255, nullable = true)
  private String section_badges;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String section1_text;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String section2_text;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String section3_text;

  @Column(nullable = false)
  private LocalDateTime created_at;
}
