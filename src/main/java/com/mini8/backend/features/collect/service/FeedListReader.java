package com.mini8.backend.features.collect.service;

import com.mini8.backend.features.collect.domain.BlogSource;
import com.mini8.backend.features.collect.domain.SourceType;
import com.mini8.backend.features.collect.domain.dto.CollectedPost;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

/**
 * 피드에 목록만 오는 곳. 컬리 하나다(결정 2-55).
 *
 * <p>주소만 피드에서 얻고 제목 · 발행 시각 · 본문은 글 페이지에서 읽는다. 피드에도 제목과 날짜가 있지만 한 군데서 읽는 편이 규칙이 하나로 유지된다.
 */
@Component
public class FeedListReader implements SourceReader {

  /** 한 번에 볼 글 수. 피드가 최신순이라 앞에서 자른다. 초기 전량 적재는 파이썬이 따로 한다. */
  private static final int MAX_ARTICLES = 20;

  private final XmlFetcher fetcher;
  private final ArticlePageReader pageReader;

  public FeedListReader(XmlFetcher fetcher, ArticlePageReader pageReader) {
    this.fetcher = fetcher;
    this.pageReader = pageReader;
  }

  @Override
  public boolean supports(SourceType type) {
    return type == SourceType.FEED_LIST;
  }

  @Override
  public List<CollectedPost> read(BlogSource source) throws Exception {
    Document feed = fetcher.fetch(source.url());
    return feed.select("item > link").stream()
        .map(Element::text)
        .limit(MAX_ARTICLES)
        .map(url -> pageReader.read(source.company(), url))
        .flatMap(java.util.Optional::stream)
        .toList();
  }
}
