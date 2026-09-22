package com.mini8.backend.features.bookmark.service;

import com.mini8.backend.commons.exception.BusinessException;
import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.User.repository.UserRepository;
import com.mini8.backend.database.blog.domain.entity.BlogPostEntity;
import com.mini8.backend.database.blog.repository.BlogPostRepository;
import com.mini8.backend.database.bookmark.domain.entity.BookmarkEntity;
import com.mini8.backend.features.bookmark.domain.dto.BookmarkResponseDTO;
import com.mini8.backend.features.bookmark.repository.BookmarkRepository;
import com.mini8.backend.features.company.domain.dto.CompanyResponseDTO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import org.flywaydb.core.api.ErrorCode;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkService {

  private final BookmarkRepository bookmarkRepository;
 


  @Transactional
public void addBookmark(Long userId, Long postId) {

    UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
            // 예외 import 추가 Readme에서는 commons에exception에존재 하지만 없음
    BlogPostEntity blogPost = blogPostRepository.findById(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

    if (bookmarkRepository.existsByUser_User_idAndBlogPost_Blog_post_id(
            userId, postId)) {

        throw new BusinessException(ErrorCode.ALREADY_BOOKMARKED);
    }

    BookmarkEntity bookmark = BookmarkEntity.builder()
            .user(user)
            .blogPost(blogPost)
            .created_at(LocalDateTime.now())
            .build();

    bookmarkRepository.save(bookmark);
  }

  public Map<String, Object> getBookmark(Long userId) {

    List<BookmarkResponseDTO> bookmarks =
        bookmarkRepository.findByUser_User_idOrderByCreated_atDesc(userId)
                .stream()
                .map(bookmark -> {

                   OffsetDateTime savedAt =bookmark.getCreated_at()
                    .atOffset(ZoneOffset.of("+09:00"));

                      return BookmarkResponseDTO.builder()
                      .bookmarkId(bookmark.getBookmark_id())
                      .postId(bookmark.getBlogPost().getBlog_post_id())
                      .companyId(bookmark.getBlogPost().getCompany().getCompany_id())
                      .title(bookmark.getBlogPost().getTitle())
                      .companyName(bookmark.getBlogPost().getCompany().getName())
                      //.categories(...)
                      .publishedAt(bookmark.getBlogPost().getPublished_at())
                      .savedAt(savedAt)
                      .hasGuide(false)
                    .build();
              })

                .toList();
    return Map.of(
            "bookmarks", bookmarks,
            "count", bookmarks.size()
    );
  }

  public BookmarkResponseDTO deleteBookmark() {
    return null;
  }
}
