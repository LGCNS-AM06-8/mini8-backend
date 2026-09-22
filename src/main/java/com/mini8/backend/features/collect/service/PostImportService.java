package com.mini8.backend.features.collect.service;

import com.mini8.backend.features.collect.domain.dto.ImportedPost;
import com.mini8.backend.features.collect.domain.dto.TechDictionary;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 수집 단계가 만든 파일을 읽어 표에 넣는 입구.
 *
 * <p>저장은 {@link PostIngestService} 가 한다. 이 클래스는 파일에서 읽어 넘기는 일만 맡는다. 나중에 웹에서 수집한 글을 넘기는 입구가 하나 더 생겨도
 * 저장 쪽은 그대로 쓴다.
 */
@Service
public class PostImportService {

  private static final Logger log = LoggerFactory.getLogger(PostImportService.class);

  private final ImportFileReader reader;
  private final PostIngestService ingest;
  private final String dataDir;

  public PostImportService(
      ImportFileReader reader,
      PostIngestService ingest,
      @Value("${collect.data-dir:}") String dataDir) {
    this.reader = reader;
    this.ingest = ingest;
    this.dataDir = dataDir;
  }

  public PostIngestService.IngestResult importFromFiles() {
    if (dataDir == null || dataDir.isBlank()) {
      throw new IllegalStateException("수집 파일 폴더가 정해지지 않았습니다. .env 에 COLLECT_DATA_DIR 을 적어 주세요.");
    }
    Path dir = Path.of(dataDir);
    if (!Files.isDirectory(dir)) {
      throw new IllegalStateException("그런 폴더가 없습니다: " + dir);
    }

    List<ImportedPost> rows = reader.readPosts(dir);
    TechDictionary dictionary = reader.readDictionary(dir);
    log.info("파일에서 글 {}편, 기술 사전 {}종을 읽었습니다", rows.size(), dictionary.finalCounts().size());

    return ingest.ingest(rows, dictionary);
  }
}
