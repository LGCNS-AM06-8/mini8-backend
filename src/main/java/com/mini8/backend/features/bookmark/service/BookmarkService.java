package com.mini8.backend.features.bookmark.service;

import com.mini8.backend.commons.exception.BusinessException;
import com.mini8.backend.commons.exception.ErrorCode;
import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.blog.domain.entity.BlogPostEntity;
import com.mini8.backend.database.bookmark.domain.entity.BookmarkEntity;
import com.mini8.backend.database.repository.BlogPostCategoryRepository;
import com.mini8.backend.database.repository.BlogPostRepository;
import com.mini8.backend.database.repository.UserRepository;
import com.mini8.backend.features.bookmark.domain.dto.BookmarkResponseDTO;
import com.mini8.backend.features.bookmark.repository.BookmarkRepository;
import com.mini8.backend.features.company.repository.CompanyPostQueryRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;
  private final UserRepository userRepository;
  private final BlogPostRepository blogPostRepository;
  private final BlogPostCategoryRepository blogPostCategoryRepository;
  // findGuidedPostIds 이 존재하는 래포 가져오기
  private final CompanyPostQueryRepository companyPostQueryRepository;

  @Transactional
  public BookmarkResponseDTO addBookmark(Long userId, Long postId) {

    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

    BlogPostEntity blogPost =
        blogPostRepository
            .findById(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

    if (bookmarkRepository.existsBookmark(userId, postId)) {
      throw new BusinessException(ErrorCode.ALREADY_BOOKMARKED);
    }

    BookmarkEntity bookmark =
        BookmarkEntity.builder()
            .user(user)
            .blogPost(blogPost)
            .created_at(LocalDateTime.now())
            .build();

    BookmarkEntity saved = bookmarkRepository.save(bookmark);
    return BookmarkResponseDTO.builder()
        .bookmarkId(saved.getBookmark_id())
        .postId(saved.getBlogPost().getBlog_post_id())
        .createdAt(saved.getCreated_at())
        .build();
  }

  public Map<String, Object> getBookmark(Long userId) {

    List<BookmarkEntity> bookmarkEntities = bookmarkRepository.findBookmarksByUser(userId);

    // 북마크 ai가이드 존재여부 판단해서 has값 수정하는 코드

    List<Long> postIds =
        bookmarkEntities.stream()
            .map(bookmark -> bookmark.getBlogPost().getBlog_post_id())
            .toList();
    // 최신 사용자의 프로필 버전 가져오기
    Integer profileVersion = userRepository.findById(userId).orElseThrow().getProfile_version();
    // 현재 프로필 버전으로 만들어진 ai가이드 있는 게시글 ID 조회
    List<Long> guidedPostIds =
        postIds.isEmpty()
            ? List.of()
            : companyPostQueryRepository.findGuidedPostIds(userId, profileVersion, postIds);

    // 글별 기술 칩. 04 글 목록(CompanyService)과 같은 조회라 순서도 같다
    Map<Long, List<String>> skillsByPost =
        postIds.isEmpty()
            ? Map.of()
            : companyPostQueryRepository.findTags(postIds).stream()
                .collect(
                    Collectors.groupingBy(
                        tag -> tag.getId().getBlog_post_id(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                            tag -> tag.getTechTag().getName(), Collectors.toList())));

    List<BookmarkResponseDTO> bookmarks =
        bookmarkEntities.stream()
            .map(
                bookmark -> {
                  BlogPostEntity post = bookmark.getBlogPost();

                  Long postId = post.getBlog_post_id();

                  List<String> categories =
                      blogPostCategoryRepository.findCategoriesByPostId(postId).stream()
                          .map(category -> category.getId().getName())
                          .toList();

                  OffsetDateTime savedAt =
                      bookmark.getCreated_at().atOffset(ZoneOffset.of("+09:00"));

                  return BookmarkResponseDTO.builder()
                      .bookmarkId(bookmark.getBookmark_id())
                      .postId(postId)
                      .companyId(post.getCompany().getCompany_id())
                      .title(post.getTitle())
                      .companyName(post.getCompany().getName())
                      .categories(categories)
                      .summary(post.getSummary())
                      .skills(skillsByPost.getOrDefault(postId, List.of()))
                      .publishedAt(post.getPublished_at().toLocalDate())
                      .savedAt(savedAt)
                      .hasGuide(guidedPostIds.contains(postId))
                      .build();
                })
            .toList();

    System.out.println("DTO 변환 완료");

    return Map.of("bookmarks", bookmarks, "count", bookmarks.size());
  }

  @Transactional
  public BookmarkResponseDTO deleteBookmark(Long userId, Long postId) {
    BookmarkEntity bookmark =
        bookmarkRepository
            .findBookmark(userId, postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

    bookmarkRepository.delete(bookmark);

    return null;
  }
}
