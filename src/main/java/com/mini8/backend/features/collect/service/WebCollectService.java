package com.mini8.backend.features.collect.service;

import com.mini8.backend.database.company.domain.entity.CompanyEntity;
import com.mini8.backend.database.repository.BlogPostRepository;
import com.mini8.backend.database.repository.TechTagRepository;
import com.mini8.backend.features.collect.domain.BlogSource;
import com.mini8.backend.features.collect.domain.SourceType;
import com.mini8.backend.features.collect.domain.dto.CollectedPost;
import com.mini8.backend.features.collect.domain.dto.ImportedPost;
import com.mini8.backend.features.collect.domain.dto.TechDictionary;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 웹에서 직접 받아 표에 넣는 입구(결정 2-81 의 두 번째 입구).
 *
 * <p>기업마다 받기 → 범위 안 새 글만 남기기 → AI 1차 분류 → 저장 순으로 돈다. 이미 있는 글은 분류 전에 걸러 AI 를 다시 부르지 않는다. 처음 돌리면
 * 12개월치가 다 들어가고, 다음부터는 새 글만 들어간다.
 *
 * <p>기업 하나가 끝날 때마다 저장한다. 중간에 멈춰도 끝난 기업의 글은 남는다.
 */
@Service
public class WebCollectService {

  private static final Logger log = LoggerFactory.getLogger(WebCollectService.class);

  private final PostIngestService ingestService;
  private final CollectService collectService;
  private final PostClassifier classifier;
  private final BlogPostRepository posts;
  private final TechTagRepository techTags;

  public WebCollectService(
      PostIngestService ingestService,
      CollectService collectService,
      PostClassifier classifier,
      BlogPostRepository posts,
      TechTagRepository techTags) {
    this.ingestService = ingestService;
    this.collectService = collectService;
    this.classifier = classifier;
    this.posts = posts;
    this.techTags = techTags;
  }

  /** 한 기업의 결과. 로그에 남긴다. */
  public record CompanyResult(
      String company,
      int received,
      int newInWindow,
      int classified,
      PostIngestService.IngestResult saved) {}

  public List<CompanyResult> run(List<CompanyEntity> targets) {
    List<CompanyResult> results = new ArrayList<>();
    for (CompanyEntity company : targets) {
      try {
        results.add(runOne(company));
      } catch (Exception e) {
        log.warn("{} 웹 수집 실패, 다음 기업으로 넘어갑니다: {}", company.getName(), e.getMessage());
      }
    }
    return results;
  }

  CompanyResult runOne(CompanyEntity company) {
    if (company.getFeed_url() == null || company.getFeed_type() == null) {
      log.warn("{} 수집 주소가 없어 건너뜁니다", company.getName());
      return new CompanyResult(company.getName(), 0, 0, 0, null);
    }
    BlogSource source =
        new BlogSource(
            company.getName(), SourceType.valueOf(company.getFeed_type()), company.getFeed_url());

    List<CollectedPost> received = collectService.collectOne(source);
    List<CollectedPost> fresh = freshInWindow(received, source);

    List<ImportedPost> rows = new ArrayList<>();
    List<String> extracted = new ArrayList<>();
    int classified = 0;
    for (CollectedPost post : fresh) {
      ImportedPost.Ai ai = classifier.classify(post).orElse(null);
      if (ai != null) {
        classified++;
        extracted.addAll(ai.core_techs());
      }
      rows.add(toRow(post, ai));
    }

    PostIngestService.IngestResult saved = ingestService.ingest(rows, dictionary(extracted));
    CompanyResult result =
        new CompanyResult(company.getName(), received.size(), fresh.size(), classified, saved);
    log.info("웹 수집 {}", result);
    return result;
  }

  /** 범위 안이고 표에 없는 글. 같은 주소가 두 번 오면 한 번만 둔다. */
  private List<CollectedPost> freshInWindow(List<CollectedPost> received, BlogSource source) {
    Set<String> seen = new LinkedHashSet<>();
    List<CollectedPost> fresh = new ArrayList<>();
    for (CollectedPost post : received) {
      if (post.url() == null
          || !source.covers(post.publishedAt())
          || !seen.add(post.url())
          || posts.existsByUrl(post.url())) {
        continue;
      }
      fresh.add(post);
    }
    return fresh;
  }

  private TechDictionary dictionary(List<String> extracted) {
    List<String> existing = techTags.findAll().stream().map(tag -> tag.getName()).toList();
    return TechNameResolver.buildDictionary(
        existing, PostIngestService.selectableNames(), extracted);
  }

  /** 수집한 글을 저장 단계가 읽는 모양으로 옮긴다. 파일 입구와 저장을 같이 쓰기 위해서다. */
  static ImportedPost toRow(CollectedPost post, ImportedPost.Ai ai) {
    List<ImportedPost.Heading> headings =
        post.headings() == null
            ? List.of()
            : post.headings().stream()
                .map(h -> new ImportedPost.Heading(h.idx(), h.level(), h.title()))
                .toList();
    return new ImportedPost(
        post.company(),
        post.externalId(),
        post.title(),
        post.url(),
        post.publishedAt().toString(),
        post.charCount(),
        post.hasCode(),
        post.sourceCategories(),
        headings,
        post.bodyHtml(),
        post.bodyText(),
        ai);
  }
}
