package com.mini8.backend.features.post.service;

import com.mini8.backend.commons.exception.BusinessException;
import com.mini8.backend.commons.exception.ErrorCode;
import com.mini8.backend.database.blog.domain.entity.BlogPostEntity;
import com.mini8.backend.database.blog.domain.entity.BlogPostSectionEntity;
import com.mini8.backend.features.post.domain.dto.PostResponseDTO;
import com.mini8.backend.features.post.domain.dto.PostResponseDTO.Section;
import com.mini8.backend.features.post.repository.PostRepository;
import com.mini8.backend.features.post.repository.PostSectionRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

  private final PostRepository postRepository;
  private final PostSectionRepository sectionRepository;

  @Transactional(readOnly = true)
  public PostResponseDTO getPost(Long id, Long userId) {
    BlogPostEntity post =
        postRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
    List<BlogPostSectionEntity> storedSections =
        sectionRepository.findByPostId(id).stream()
            .sorted(Comparator.comparing(BlogPostSectionEntity::getSeq))
            .toList();

    if (storedSections.isEmpty()) {
      return fallbackResponse(post, userId);
    }

    List<Section> sections = new ArrayList<>();
    for (int i = 0; i < storedSections.size(); i++) {
      BlogPostSectionEntity section = storedSections.get(i);
      sections.add(new Section(i + 1, section.getHeading(), section.getChar_count()));
    }
    return response(
        post, userId, addSectionAnchors(post.getContent_html(), sections.size()), sections);
  }

  private PostResponseDTO fallbackResponse(BlogPostEntity post, Long userId) {
    Document document = Jsoup.parseBodyFragment(post.getContent_html());
    Element wrapper = document.createElement("div").attr("id", "section-1");
    for (Node node : new ArrayList<>(document.body().childNodes())) {
      wrapper.appendChild(node);
    }
    document.body().appendChild(wrapper);

    Section section = new Section(1, post.getTitle(), post.getChar_count());
    return response(post, userId, document.body().html(), List.of(section));
  }

  private PostResponseDTO response(
      BlogPostEntity post, Long userId, String contentHtml, List<Section> sections) {
    return new PostResponseDTO(
        post.getBlog_post_id(),
        post.getTitle(),
        post.getPublished_at().toLocalDate(),
        post.getCompany().getName(),
        postRepository.findCategoryNamesByPostId(post.getBlog_post_id()),
        postRepository.findSkillNamesByPostId(post.getBlog_post_id()),
        post.getChar_count(),
        post.getUrl(),
        contentHtml,
        sections,
        postRepository.countBookmarkByUserIdAndPostId(userId, post.getBlog_post_id()) > 0);
  }

  private String addSectionAnchors(String contentHtml, int sectionCount) {
    Document document = Jsoup.parseBodyFragment(contentHtml);
    Element body = document.body();
    body.select("[id^=section-]").stream()
        .filter(element -> element.id().matches("section-\\d+"))
        .forEach(element -> element.removeAttr("id"));

    List<Element> headings =
        body.select("h2, h3").stream().filter(heading -> !heading.text().isBlank()).toList();
    for (int seq = 1; seq <= sectionCount; seq++) {
      if (seq <= headings.size()) {
        headings.get(seq - 1).attr("id", "section-" + seq);
      } else {
        body.appendElement("span").attr("id", "section-" + seq);
      }
    }
    return body.html();
  }
}
