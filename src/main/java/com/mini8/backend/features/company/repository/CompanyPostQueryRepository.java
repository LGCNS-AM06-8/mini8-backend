package com.mini8.backend.features.company.repository;

import com.mini8.backend.database.blog.domain.entity.BlogPostEntity;
import com.mini8.backend.database.blog.domain.entity.BlogPostTagEntity;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

public interface CompanyPostQueryRepository extends Repository<BlogPostEntity, Long> {

  @Query(
      """
      select p from BlogPostEntity p
      where p.company.company_id = :companyId
        and (p.is_tech = true or p.is_tech is null)
      order by p.published_at desc
      """)
  List<BlogPostEntity> findEligiblePosts(@Param("companyId") Long companyId);

  @Query("select f.jobField from UserJobFieldEntity f where f.user.user_id = :userId order by f.id")
  List<String> findJobFields(@Param("userId") Long userId);

  @Query(
      """
      select s.techTag.name from UserSkillEntity s
      where s.user.user_id = :userId and s.skill_type = 'WANT'
      order by s.techTag.tech_tag_id
      """)
  List<String> findWantSkillNames(@Param("userId") Long userId);

  @Query(
      """
      select t from BlogPostTagEntity t join fetch t.techTag
      where t.id.blog_post_id in :postIds
      order by t.id.blog_post_id,
        case when t.tag_rank is null then 1 else 0 end,
        t.tag_rank,
        t.techTag.name
      """)
  List<BlogPostTagEntity> findTags(@Param("postIds") List<Long> postIds);

  @Query(
      """
      select s.blogPost.blog_post_id as postId, count(s) as sectionCount
      from BlogPostSectionEntity s
      where s.blogPost.blog_post_id in :postIds
      group by s.blogPost.blog_post_id
      """)
  List<SectionCount> findSectionCounts(@Param("postIds") List<Long> postIds);

  @Query(
      """
      select b.blogPost.blog_post_id from BookmarkEntity b
      where b.user.user_id = :userId and b.blogPost.blog_post_id in :postIds
      """)
  List<Long> findBookmarkedPostIds(
      @Param("userId") Long userId, @Param("postIds") List<Long> postIds);

  @Query(
      """
      select distinct g.blogPost.blog_post_id from AiGuideEntity g
      where g.user.user_id = :userId
        and g.profile_version = :profileVersion
        and g.blogPost.blog_post_id in :postIds
      """)
  List<Long> findGuidedPostIds(
      @Param("userId") Long userId,
      @Param("profileVersion") Integer profileVersion,
      @Param("postIds") List<Long> postIds);

  interface SectionCount {
    Long getPostId();

    Long getSectionCount();
  }
}
