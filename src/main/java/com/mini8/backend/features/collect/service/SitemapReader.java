package com.mini8.backend.features.collect.service;

import com.mini8.backend.features.collect.domain.BlogSource;
import com.mini8.backend.features.collect.domain.SourceType;
import com.mini8.backend.features.collect.domain.dto.CollectedPost;
import java.util.List;
import java.util.Optional;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

/**
 * 사이트맵에서 주소를 얻는 곳. LY 하나다(결정 2-55).
 *
 * <p>사이트맵에 제목도 발행일도 없다. 주소 1,507개 중 글은 210개이고 나머지는 태그 · 쪽 나눔 · 다른 언어 경로다(09-21 실측).
 */
@Component
public class SitemapReader implements SourceReader {

  /** 한 번에 볼 글 수. 사이트맵이 오래된 순이라 뒤에서 자른다. 초기 전량 적재는 파이썬이 따로 한다. */
  private static final int MAX_ARTICLES = 20;

  private final XmlFetcher fetcher;
  private final ArticlePageReader pageReader;

  public SitemapReader(XmlFetcher fetcher, ArticlePageReader pageReader) {
    this.fetcher = fetcher;
    this.pageReader = pageReader;
  }

  @Override
  public boolean supports(SourceType type) {
    return type == SourceType.SITEMAP;
  }

  @Override
  public List<CollectedPost> read(BlogSource source) throws Exception {
    Document sitemap = fetcher.fetch(source.url());
    List<String> urls = articleUrls(sitemap);
    List<String> recent = urls.subList(Math.max(0, urls.size() - MAX_ARTICLES), urls.size());
    return recent.stream()
        .map(url -> pageReader.read(source.company(), url))
        .flatMap(Optional::stream)
        .toList();
  }

  /** 한국어 글만 남긴다. 태그 목록과 쪽 나눔은 글이 아니다. */
  public List<String> articleUrls(Document sitemap) {
    return sitemap.select("loc").stream()
        .map(Element::text)
        .filter(url -> url.contains("/ko/"))
        .filter(url -> !url.contains("/tag/") && !url.contains("/page/"))
        .toList();
  }
}
