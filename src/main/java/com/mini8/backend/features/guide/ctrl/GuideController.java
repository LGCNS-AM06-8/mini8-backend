package com.mini8.backend.features.guide.ctrl;

import com.mini8.backend.features.guide.service.GuideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class GuideController {

  private final GuideService guideService;

  // generateGuide: 게시글에 대한 AI 가이드 생성
  @PostMapping("/{id}/guide")
  public ResponseEntity<?> generateGuide(@PathVariable("id") int id) {
    return null;
  }
}
