package com.mini8.backend.features.collect.service;

import com.mini8.backend.commons.exception.BusinessException;
import com.mini8.backend.commons.exception.ErrorCode;
import com.mini8.backend.database.company.domain.entity.CompanyEntity;
import com.mini8.backend.database.repository.CompanyRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 웹 수집을 뒤에서 돌린다. 기업 8곳을 받고 글마다 AI 를 부르면 수십 분이 걸려서 요청은 바로 202 로 돌려준다.
 *
 * <p>한 번에 하나만 돈다. 도는 중에 또 부르면 새로 시작하지 않고 돌고 있는 작업을 알려 준다. 같은 글을 두 작업이 동시에 넣지 않게 하기 위해서다.
 */
@Service
public class CollectJobService {

  private static final Logger log = LoggerFactory.getLogger(CollectJobService.class);
  private static final ZoneOffset KST = ZoneOffset.of("+09:00");
  private static final DateTimeFormatter ID_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

  private final WebCollectService webCollectService;
  private final PostIngestService ingestService;
  private final CompanyRepository companies;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final AtomicReference<Job> running = new AtomicReference<>();

  public CollectJobService(
      WebCollectService webCollectService,
      PostIngestService ingestService,
      CompanyRepository companies) {
    this.webCollectService = webCollectService;
    this.ingestService = ingestService;
    this.companies = companies;
  }

  /** 명세 202 응답 모양. */
  public record Job(String jobId, OffsetDateTime startedAt) {}

  /** companyId 가 없으면 전체 기업을 받는다. 없는 기업이면 404. */
  public synchronized Job start(Long companyId) {
    Job current = running.get();
    if (current != null) {
      log.info("웹 수집이 이미 돌고 있어 새로 시작하지 않습니다: {}", current.jobId());
      return current;
    }
    List<CompanyEntity> targets = targets(companyId);

    OffsetDateTime now = OffsetDateTime.now(KST).withNano(0);
    Job job = new Job("collect-" + now.format(ID_FORMAT), now);
    running.set(job);
    executor.submit(() -> runJob(job, targets));
    return job;
  }

  private List<CompanyEntity> targets(Long companyId) {
    List<CompanyEntity> all = new ArrayList<>(ingestService.ensureCompanies().values());
    if (companyId == null) {
      return all;
    }
    return List.of(
        companies
            .findById(companyId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND)));
  }

  private void runJob(Job job, List<CompanyEntity> targets) {
    log.info("웹 수집 시작 {} (기업 {}곳)", job.jobId(), targets.size());
    try {
      List<WebCollectService.CompanyResult> results = webCollectService.run(targets);
      int saved =
          results.stream().filter(r -> r.saved() != null).mapToInt(r -> r.saved().newPosts()).sum();
      log.info("웹 수집 끝 {} (새 글 {}편)", job.jobId(), saved);
    } catch (Exception e) {
      log.error("웹 수집 중단 {}", job.jobId(), e);
    } finally {
      running.set(null);
    }
  }
}
