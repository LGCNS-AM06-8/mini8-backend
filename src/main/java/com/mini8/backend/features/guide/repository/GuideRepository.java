package com.mini8.backend.features.guide.repository;

import com.mini8.backend.database.ai_guide.domain.entity.AiGuideEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GuideRepository extends JpaRepository<AiGuideEntity, Long> {

  @Query(
      "select guide from AiGuideEntity guide "
          + "where guide.user.user_id = :userId "
          + "and guide.blogPost.blog_post_id = :postId "
          + "and guide.profile_version = :profileVersion "
          + "and guide.prompt_version = :promptVersion")
  Optional<AiGuideEntity> findCached(
      @Param("userId") Long userId,
      @Param("postId") Long postId,
      @Param("profileVersion") Integer profileVersion,
      @Param("promptVersion") String promptVersion);
}
