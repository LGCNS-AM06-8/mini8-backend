package com.mini8.backend.features.bookmark.ctrl;

import com.mini8.backend.features.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

  private final BookmarkService bookmarkService;

  // addBookmark: 북마크에 글 추가
  @PostMapping("/{postId}")
  public ResponseEntity<?> addBookmark(@PathVariable("userId") int userId) {
    return null;
  }

  // getBookmark: 북마크 조회
  @GetMapping("/")
  public ResponseEntity<?> getBookmark() {
    return null;
  }

  // deleteBookmark: 북마크 삭제
  @DeleteMapping("/{postId}")
  public ResponseEntity<?> deleteBookmark(@PathVariable("userId") int userId) {
    return null;
  }
}
