package com.mini8.backend.features.post.repository;

import com.mini8.backend.database.blog.domain.entity.BlogPostEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<BlogPostEntity, Long> {

  @Query(
      "select category.id.name from BlogPostCategoryEntity category "
          + "where category.blogPost.blog_post_id = :postId order by category.id.name")
  List<String> findCategoryNamesByPostId(@Param("postId") Long postId);

  @Query(
      "select tag.techTag.name from BlogPostTagEntity tag "
          + "where tag.blogPost.blog_post_id = :postId "
          + "order by coalesce(tag.tag_rank, 2147483647), tag.techTag.name")
  List<String> findSkillNamesByPostId(@Param("postId") Long postId);

  @Query(
      "select count(bookmark) from BookmarkEntity bookmark "
          + "where bookmark.user.user_id = :userId "
          + "and bookmark.blogPost.blog_post_id = :postId")
  long countBookmarkByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
}
