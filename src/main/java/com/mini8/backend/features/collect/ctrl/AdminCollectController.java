package com.mini8.backend.features.collect.ctrl;

import com.mini8.backend.features.collect.service.PostImportService;
import com.mini8.backend.features.collect.service.PostIngestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자용 적재 입구.
 *
 * <p>아직 권한을 걸지 않았다. 관리자 계정을 만드는 길이 없어 로그인해도 모두 USER 로 들어오기 때문이다. 계정이 생기면 SecurityConfig 에서
 * /api/admin/** 을 ADMIN 으로 조인다.
 */
@RestController
@RequestMapping("/api/admin/collect")
@Tag(name = "관리자 · 적재", description = "수집한 글을 표에 넣는다. 운영자만 부른다")
public class AdminCollectController {

  private final PostImportService importService;

  public AdminCollectController(PostImportService importService) {
    this.importService = importService;
  }

  @PostMapping("/import")
  @Operation(
      summary = "파일에서 글 적재",
      description =
          "수집 단계가 만든 파일(classified.jsonl · posts_12m*.jsonl · tech_dict.json)을 읽어 표에 넣는다. "
              + "폴더 경로는 .env 의 COLLECT_DATA_DIR 에서 읽는다. "
              + "이미 있는 글은 url 로 걸러내므로 여러 번 불러도 행이 늘지 않는다.")
  public ResponseEntity<PostIngestService.IngestResult> importFromFiles() {
    return ResponseEntity.ok(importService.importFromFiles());
  }
}
