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
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;
  private final UserRepository userRepository;
  private final BlogPostRepository blogPostRepository;
  private final BlogPostCategoryRepository blogPostCategoryRepository;

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
                      .publishedAt(post.getPublished_at().toLocalDate())
                      .savedAt(savedAt)
                      .hasGuide(false)
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
