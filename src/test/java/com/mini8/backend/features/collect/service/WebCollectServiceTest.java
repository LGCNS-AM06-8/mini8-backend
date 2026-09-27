package com.mini8.backend.features.collect.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mini8.backend.database.company.domain.entity.CompanyEntity;
import com.mini8.backend.database.repository.BlogPostRepository;
import com.mini8.backend.database.repository.BlogPostTagRepository;
import com.mini8.backend.features.collect.domain.dto.CollectedPost;
import com.mini8.backend.features.collect.domain.dto.ImportedPost;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

/** 웹 수집 한 바퀴. 받아오기와 AI 는 가짜로 두고 거르기 · 저장만 실제로 본다. */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WebCollectServiceTest {

  @Autowired WebCollectService service;
  @Autowired PostIngestService ingestService;
  @Autowired BlogPostRepository posts;
  @Autowired BlogPostTagRepository tags;

  @MockitoBean CollectService collectService;
  @MockitoBean PostClassifier classifier;

  private CollectedPost post(String url, LocalDateTime publishedAt) {
    return new CollectedPost(
        "토스",
        url,
        "제목 " + url,
        url,
        publishedAt,
        "<h2>들어가며</h2><p>본문</p>",
        "들어가며 본문",
        7,
        false,
        List.of(new CollectedPost.Heading(1, "h2", "들어가며")),
        List.of("Backend"));
  }

  @Test
  void 범위_안_새_글만_분류해_넣고_두_번째에는_AI_를_부르지_않는다() {
    CompanyEntity toss = ingestService.ensureCompanies().get("토스");
    LocalDateTime recent = LocalDateTime.now().minusDays(3);
    when(collectService.collectOne(any()))
        .thenReturn(
            List.of(
                post("https://toss.tech/a", recent),
                post("https://toss.tech/a", recent),
                post("https://toss.tech/old", LocalDateTime.now().minusMonths(13))));
    when(classifier.classify(any()))
        .thenReturn(
            Optional.of(
                new ImportedPost.Ai(true, List.of("kafka", "GraphQL"), "Backend", "중급", "요지")));

    WebCollectService.CompanyResult first = service.runOne(toss);
    WebCollectService.CompanyResult second = service.runOne(toss);

    assertThat(first.newInWindow()).isEqualTo(1);
    assertThat(first.saved().newPosts()).isEqualTo(1);
    assertThat(second.newInWindow()).isZero();
    verify(classifier, times(1)).classify(any());
    assertThat(posts.findByUrl("https://toss.tech/old")).isEmpty();

    var saved = posts.findByUrl("https://toss.tech/a").orElseThrow();
    assertThat(saved.getField()).isEqualTo("Backend");
    assertThat(saved.getIs_tech()).isTrue();
    assertThat(tags.findAll())
        .extracting(tag -> tag.getTechTag().getName())
        .containsExactly("Kafka", "GraphQL");
  }

  @Test
  void 분류에_실패한_글은_분류값_없이_넣는다() {
    CompanyEntity toss = ingestService.ensureCompanies().get("토스");
    when(collectService.collectOne(any()))
        .thenReturn(List.of(post("https://toss.tech/b", LocalDateTime.now().minusDays(1))));
    when(classifier.classify(any())).thenReturn(Optional.empty());

    service.runOne(toss);

    var saved = posts.findByUrl("https://toss.tech/b").orElseThrow();
    assertThat(saved.getIs_tech()).isNull();
    assertThat(saved.getField()).isNull();
  }

  @Test
  void 수집_주소가_없는_기업은_받지_않는다() {
    CompanyEntity empty = CompanyEntity.builder().name("주소없음").build();

    WebCollectService.CompanyResult result = service.runOne(empty);

    assertThat(result.received()).isZero();
    verify(collectService, never()).collectOne(any());
  }
}
