package com.mini8.backend.features.guide.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini8.backend.commons.exception.BusinessException;
import com.mini8.backend.commons.exception.ErrorCode;
import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.ai_guide.domain.entity.AiGuideEntity;
import com.mini8.backend.database.blog.domain.entity.BlogPostEntity;
import com.mini8.backend.database.blog.domain.entity.BlogPostSectionEntity;
import com.mini8.backend.database.repository.BlogPostRepository;
import com.mini8.backend.database.repository.UserRepository;
import com.mini8.backend.features.guide.domain.dto.GuideResponseDTO;
import com.mini8.backend.features.guide.domain.dto.GuideResponseDTO.Basis;
import com.mini8.backend.features.guide.domain.dto.GuideResponseDTO.SectionBadge;
import com.mini8.backend.features.guide.repository.GuideRepository;
import com.mini8.backend.features.guide.repository.GuideSectionRepository;
import com.mini8.backend.features.user.domain.dto.ProfileResponseDTO;
import com.mini8.backend.features.user.domain.dto.ProfileResponseDTO.SkillResponseDTO;
import com.mini8.backend.features.user.service.ProfileService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class GuideService {

  private static final Set<String> OUTPUT_KEYS =
      Set.of("focusSections", "sectionBadges", "section1Text", "section2Text", "section3Text");
  private static final Set<String> BADGE_KEYS = Set.of("seq", "badge");
  private static final Set<String> BADGES = Set.of("KEY", "LIGHT", "SKIP", "REF");

  private final ProfileService profileService;
  private final UserRepository userRepository;
  private final BlogPostRepository postRepository;
  private final GuideRepository guideRepository;
  private final GuideSectionRepository sectionRepository;
  private final GeminiGuideClient geminiClient;
  private final ObjectMapper objectMapper;
  private final String promptVersion;

  public GuideService(
      ProfileService profileService,
      UserRepository userRepository,
      BlogPostRepository postRepository,
      GuideRepository guideRepository,
      GuideSectionRepository sectionRepository,
      GeminiGuideClient geminiClient,
      ObjectMapper objectMapper,
      @Value("${gemini.prompt-version}") String promptVersion) {
    this.profileService = profileService;
    this.userRepository = userRepository;
    this.postRepository = postRepository;
    this.guideRepository = guideRepository;
    this.sectionRepository = sectionRepository;
    this.geminiClient = geminiClient;
    this.objectMapper = objectMapper;
    this.promptVersion = promptVersion;
  }

  public GuideResponseDTO generateGuide(Long userId, Long postId) {
    ProfileResponseDTO profile = profileService.getProfile(userId);
    if (profile.getCareerYears() == null) {
      throw new BusinessException(ErrorCode.PROFILE_REQUIRED);
    }

    BlogPostEntity post =
        postRepository
            .findById(postId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

    return guideRepository
        .findCached(userId, postId, profile.getProfileVersion(), promptVersion)
        .map(guide -> response(guide, profile, true))
        .orElseGet(() -> generate(profile, post));
  }

  private GuideResponseDTO generate(ProfileResponseDTO profile, BlogPostEntity post) {
    List<BlogPostSectionEntity> sections = sectionRepository.findByPostId(post.getBlog_post_id());
    GeneratedGuide generated =
        parseAndValidate(callGemini(buildPrompt(profile, post, sections)), sections.size());

    UserEntity user = userRepository.getReferenceById(profile.getUserId());
    AiGuideEntity guide =
        AiGuideEntity.builder()
            .user(user)
            .blogPost(post)
            .profile_version(profile.getProfileVersion())
            .prompt_version(promptVersion)
            .focus_sections(writeJson(generated.focusSections()))
            .section_badges(writeJson(generated.sectionBadges()))
            .section1_text(generated.section1Text())
            .section2_text(generated.section2Text())
            .section3_text(generated.section3Text())
            .created_at(LocalDateTime.now())
            .build();

    try {
      return response(guideRepository.save(guide), profile, false);
    } catch (DataIntegrityViolationException exception) {
      return guideRepository
          .findCached(
              profile.getUserId(),
              post.getBlog_post_id(),
              profile.getProfileVersion(),
              promptVersion)
          .map(saved -> response(saved, profile, true))
          .orElseThrow(this::aiUnavailable);
    }
  }

  private String callGemini(String prompt) {
    try {
      return geminiClient.generate(prompt);
    } catch (Exception exception) {
      throw aiUnavailable();
    }
  }

  private GeneratedGuide parseAndValidate(String raw, int sectionCount) {
    try {
      JsonNode root = objectMapper.readTree(raw);
      if (!root.isObject() || !fieldNames(root).equals(OUTPUT_KEYS)) {
        throw aiUnavailable();
      }

      JsonNode badges = root.get("sectionBadges");
      if (!badges.isArray()) {
        throw aiUnavailable();
      }
      badges.forEach(
          badge -> {
            if (!badge.isObject() || !fieldNames(badge).equals(BADGE_KEYS)) {
              throw aiUnavailable();
            }
          });

      GeneratedGuide generated = objectMapper.treeToValue(root, GeneratedGuide.class);
      validate(generated, sectionCount);
      return generated;
    } catch (BusinessException exception) {
      throw exception;
    } catch (Exception exception) {
      throw aiUnavailable();
    }
  }

  private void validate(GeneratedGuide generated, int sectionCount) {
    if (generated.focusSections() == null
        || generated.sectionBadges() == null
        || generated.sectionBadges().size() != sectionCount
        || generated.section1Text() == null
        || generated.section2Text() == null
        || generated.section3Text() == null) {
      throw aiUnavailable();
    }

    List<Integer> keySections = new ArrayList<>();
    for (int index = 0; index < generated.sectionBadges().size(); index++) {
      SectionBadge sectionBadge = generated.sectionBadges().get(index);
      if (sectionBadge == null
          || sectionBadge.seq() != index + 1
          || !BADGES.contains(sectionBadge.badge())) {
        throw aiUnavailable();
      }
      if ("KEY".equals(sectionBadge.badge())) {
        keySections.add(sectionBadge.seq());
      }
    }

    if (sectionCount > 1 && keySections.size() == sectionCount) {
      throw aiUnavailable();
    }

    if (generated.focusSections().stream()
            .anyMatch(seq -> seq == null || seq < 1 || seq > sectionCount)
        || new LinkedHashSet<>(generated.focusSections()).size() != generated.focusSections().size()
        || !generated.focusSections().equals(keySections)) {
      throw aiUnavailable();
    }
  }

  private GuideResponseDTO response(
      AiGuideEntity guide, ProfileResponseDTO profile, boolean cached) {
    return new GuideResponseDTO(
        guide.getAi_guide_id(),
        cached,
        basis(profile),
        readJson(guide.getFocus_sections(), new TypeReference<List<Integer>>() {}),
        readJson(guide.getSection_badges(), new TypeReference<List<SectionBadge>>() {}),
        guide.getSection1_text(),
        guide.getSection2_text(),
        guide.getSection3_text());
  }

  private Basis basis(ProfileResponseDTO profile) {
    return new Basis(
        profile.getCareerYears(), names(profile.getHaveSkills()), names(profile.getWantSkills()));
  }

  private List<String> names(List<SkillResponseDTO> skills) {
    return skills.stream().map(SkillResponseDTO::name).toList();
  }

  private String buildPrompt(
      ProfileResponseDTO profile, BlogPostEntity post, List<BlogPostSectionEntity> sections) {
    StringBuilder input =
        new StringBuilder(loadPrompt())
            .append("\n\n[프로필]\n경력 연차: ")
            .append(profile.getCareerYears())
            .append("\n희망 직무: ")
            .append(String.join(", ", profile.getJobFields()))
            .append("\n보유 기술: ")
            .append(String.join(", ", names(profile.getHaveSkills())))
            .append("\n관심 기술: ")
            .append(String.join(", ", names(profile.getWantSkills())))
            .append("\n\n[제목]\n")
            .append(post.getTitle())
            .append("\n\n[계열]\n")
            .append(post.getField())
            .append("\n\n[난이도]\n")
            .append(post.getLevel())
            .append("\n\n[본문 구간]");

    for (int index = 0; index < sections.size(); index++) {
      BlogPostSectionEntity section = sections.get(index);
      input
          .append("\n\n[")
          .append(index + 1)
          .append("]\n제목: ")
          .append(section.getHeading())
          .append("\n내용: ")
          .append(section.getContent_text());
    }
    return input.toString();
  }

  private String loadPrompt() {
    try {
      return new ClassPathResource("prompts/guide-v1.txt")
          .getContentAsString(StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw aiUnavailable();
    }
  }

  private String writeJson(Object value) {
    try {
      return objectMapper.writeValueAsString(value);
    } catch (JsonProcessingException exception) {
      throw aiUnavailable();
    }
  }

  private <T> T readJson(String value, TypeReference<T> type) {
    try {
      return objectMapper.readValue(value, type);
    } catch (JsonProcessingException exception) {
      throw aiUnavailable();
    }
  }

  private Set<String> fieldNames(JsonNode node) {
    Set<String> fields = new LinkedHashSet<>();
    node.fieldNames().forEachRemaining(fields::add);
    return fields;
  }

  private BusinessException aiUnavailable() {
    return new BusinessException(ErrorCode.AI_UNAVAILABLE);
  }

  private record GeneratedGuide(
      List<Integer> focusSections,
      List<SectionBadge> sectionBadges,
      String section1Text,
      String section2Text,
      String section3Text) {}
}
