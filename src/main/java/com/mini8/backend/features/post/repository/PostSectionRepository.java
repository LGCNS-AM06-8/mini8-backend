package com.mini8.backend.features.post.repository;

import com.mini8.backend.database.blog.domain.entity.BlogPostSectionEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostSectionRepository extends JpaRepository<BlogPostSectionEntity, Long> {

  @Query(
      "select section from BlogPostSectionEntity section where section.blogPost.blog_post_id = :postId")
  List<BlogPostSectionEntity> findByPostId(@Param("postId") Long postId);
}
