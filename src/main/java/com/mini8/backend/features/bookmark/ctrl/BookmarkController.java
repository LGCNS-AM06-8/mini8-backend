package com.mini8.backend.features.bookmark.ctrl;

import com.mini8.backend.features.bookmark.service.BookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  @Operation(summary = "북마크에 글 추가", description = "북마크에 글을 추가함")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "북마크 추가 성공"),
    @ApiResponse(responseCode = "401", description = "북마크 추가 실패(토큰 유효성 확인)"),
    @ApiResponse(responseCode = "404", description = "북마크 추가 실패(유효하지 않은 postId)"),
    @ApiResponse(responseCode = "409", description = "북마크 추가 실패(이미 존재하는 글)")
  })
  @PostMapping("/{postId}")
  public ResponseEntity<?> addBookmark(@AuthenticationPrincipal Long userId,@PathVariable("postId") Long postId) {
    return null;
  }

  // getBookmark: 북마크 조회
  @Operation(summary = "북마크 조회", description = "북마크에 저장된 글을 조회함")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "북마크 조회 성공"),
    @ApiResponse(responseCode = "401", description = "북마크 조회 실패(토큰 유효성 확인)")
  })
  @GetMapping
  public ResponseEntity<?> getBookmark() {
    return null;
  }

  // deleteBookmark: 북마크 삭제
  @Operation(summary = "북마크 삭제", description = "글을 북마크에서 제외함")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "북마크 삭제 성공"),
    @ApiResponse(responseCode = "401", description = "북마크 삭제 실패(토큰 유효성 확인)"),
    @ApiResponse(responseCode = "404", description = "북마크 삭제 실패(해당 글을 북마크에서 찾을 수 없음)")
  })
  @DeleteMapping("/{postId}")
  public ResponseEntity<?> deleteBookmark(@PathVariable("postId") Long postId) {
    return null;
  }
}
