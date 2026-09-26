package com.mini8.backend.features.company.service;

import com.mini8.backend.commons.exception.BusinessException;
import com.mini8.backend.commons.exception.ErrorCode;
import com.mini8.backend.database.User.domain.entity.UserEntity;
import com.mini8.backend.database.blog.domain.entity.BlogPostEntity;
import com.mini8.backend.database.company.domain.entity.CompanyEntity;
import com.mini8.backend.database.repository.BlogPostRepository;
import com.mini8.backend.database.repository.CompanyRepository;
import com.mini8.backend.database.repository.UserRepository;
import com.mini8.backend.database.repository.BlogPostCategoryRepository;
import com.mini8.backend.features.company.domain.dto.CompanyPostListResponseDTO;
import com.mini8.backend.features.company.domain.dto.CompanyPostListResponseDTO.Filter;
import com.mini8.backend.features.company.domain.dto.CompanyPostListResponseDTO.Post;
import com.mini8.backend.features.company.domain.dto.CompanyResponseDTO;
import com.mini8.backend.features.company.domain.dto.CompanyStatsDTO;
import com.mini8.backend.features.company.repository.CompanyPostQueryRepository;
import jakarta.transaction.Transactional;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

  private final CompanyRepository companyRepository;
  private final UserRepository userRepository;
  private final CompanyPostQueryRepository companyPostQueryRepository;
  private final BlogPostRepository blogPostRepository;
  private final BlogPostCategoryRepository blogPostCategoryRepository;
  public CompanyResponseDTO getCompanyList() {
    return null;
  }

  @Transactional
  public CompanyPostListResponseDTO getCompanyPostList(
      Long companyId, boolean onlyMySkills, Long userId) {
    if (!companyRepository.existsById(companyId)) {
      throw new BusinessException(ErrorCode.NOT_FOUND);
    }

    List<BlogPostEntity> posts = companyPostQueryRepository.findEligiblePosts(companyId);
    if (posts.isEmpty()) {
      return new CompanyPostListResponseDTO(new Filter(onlyMySkills, 0, 0), List.of());
    }

    UserEntity user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
    List<String> jobFields = companyPostQueryRepository.findJobFields(userId);
    List<String> wantSkills = companyPostQueryRepository.findWantSkillNames(userId);
    List<Long> postIds = posts.stream().map(BlogPostEntity::getBlog_post_id).toList();

    Map<Long, List<String>> skills = skills(postIds);
    Map<Long, Long> sectionCounts = sectionCounts(postIds);
    Set<Long> bookmarkedPostIds =
        new HashSet<>(companyPostQueryRepository.findBookmarkedPostIds(userId, postIds));
    Set<Long> guidedPostIds =
        new HashSet<>(
            companyPostQueryRepository.findGuidedPostIds(
                userId, user.getProfile_version(), postIds));

    List<PostMatch> matches =
        posts.stream()
            .map(
                post -> {
                  List<String> postSkills = skills.getOrDefault(post.getBlog_post_id(), List.of());
                  List<String> matchedSkills = matchedSkills(postSkills, wantSkills);
                  boolean fieldMatches =
                      "General".equals(post.getField())
                          || (post.getField() != null && jobFields.contains(post.getField()));
                  Post response =
                      new Post(
                          post.getBlog_post_id(),
                          post.getTitle(),
                          post.getPublished_at().toLocalDate(),
                          post.getField() == null ? List.of() : List.of(post.getField()),
                          post.getLevel(),
                          post.getSummary(),
                          postSkills,
                          matchedSkills,
                          post.getChar_count(),
                          sectionCounts.getOrDefault(post.getBlog_post_id(), 0L).intValue(),
                          post.getUrl(),
                          bookmarkedPostIds.contains(post.getBlog_post_id()),
                          guidedPostIds.contains(post.getBlog_post_id()));
                  return new PostMatch(response, fieldMatches && !matchedSkills.isEmpty());
                })
            .toList();

    List<Post> personalized =
        matches.stream()
            .filter(PostMatch::personalized)
            .map(PostMatch::post)
            .sorted(
                Comparator.comparingInt((Post post) -> post.matchedSkills().size())
                    .reversed()
                    .thenComparing(Post::publishedAt, Comparator.reverseOrder()))
            .toList();
    List<Post> result =
        onlyMySkills
            ? personalized
            : matches.stream()
                .map(PostMatch::post)
                .sorted(Comparator.comparing(Post::publishedAt).reversed())
                .toList();

    return new CompanyPostListResponseDTO(
        new Filter(onlyMySkills, personalized.size(), posts.size()), result);
  }

  public CompanyResponseDTO getCompanyDetail(Long id) {

    Long companyId = id;
   
    CompanyEntity company = companyRepository.findById(companyId)
                .orElseThrow(() ->
                        new BusinessException(ErrorCode.NOT_FOUND));

    // 해당 companty_id 에 post 수 구하는 코드
    long postCount =blogPostRepository.countByCompanyId(id);
    // 해당 companty_id 에 최신 글 날짜 쿠하는 코드
    LocalDateTime firstPublishedAt =blogPostRepository.findFirstPublishedAt(id);

    // 해당 companty_id 에 오래된 글 날짜 쿠하는 코드
    LocalDateTime lastPublishedAt =blogPostRepository.findLastPublishedAt(id);
    // 카테고리 상위 5개 추출하는 코드
    List<Map<String, Object>> topCategories =
        blogPostCategoryRepository.findTopCategories(id)
                .stream()
                .limit(5)
                .map(row -> Map.of(
                        "name", row[0],
                        "count", ((Number) row[1]).intValue()
                ))
                .toList();
    // 기술 태그 상위 5개 가져오는 코드 
    List<BlogPostEntity> eligiblePosts =
        companyPostQueryRepository.findEligiblePosts(companyId);

    List<Long> postIds = eligiblePosts.stream()
            .map(BlogPostEntity::getBlog_post_id)
            .toList();

    List<Map<String, Object>> topSkills =
            companyPostQueryRepository.findTags(postIds).stream()
                    .map(tag -> tag.getTechTag().getName())
                    .collect(Collectors.groupingBy(
                            Function.identity(),
                            Collectors.counting()
                    ))
                    .entrySet().stream()
                    .sorted(
                            Map.Entry.<String, Long>comparingByValue()
                                    .reversed()
                    )
                    .limit(5)
                    .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("count", entry.getValue().intValue());
                    return map;
                })
                    .toList();


    CompanyStatsDTO stats = CompanyStatsDTO.builder()
        .postCount((int) postCount)
        .firstPublishedAt(firstPublishedAt != null? firstPublishedAt.toLocalDate(): null)
        .lastPublishedAt(lastPublishedAt != null? lastPublishedAt.toLocalDate(): null)
        .topCategories(topCategories)
        .topSkills(topSkills)
        .build();


    List<Object[]> categoryResults = blogPostCategoryRepository.findTopCategories(id);
    for (Object[] row : categoryResults) {
   // System.out.println("category = " + row[0] +", count = " + row[1]);
    }
    return CompanyResponseDTO.builder()
                .companyId(company.getCompany_id())
                .name(company.getName())
                .logoUrl(company.getLogo_url())
                .summary(company.getSummary())
                .mainBusiness(company.getMain_business())
                .sourceUrl(company.getSource_url())
                .checkedAt(LocalDate.now())
                .stats(stats)
                .build();
  }

  private Map<Long, List<String>> skills(List<Long> postIds) {
    return companyPostQueryRepository.findTags(postIds).stream()
        .collect(
            Collectors.groupingBy(
                tag -> tag.getId().getBlog_post_id(),
                LinkedHashMap::new,
                Collectors.mapping(tag -> tag.getTechTag().getName(), Collectors.toList())));
  }

  private Map<Long, Long> sectionCounts(List<Long> postIds) {
    return companyPostQueryRepository.findSectionCounts(postIds).stream()
        .collect(
            Collectors.toMap(
                CompanyPostQueryRepository.SectionCount::getPostId,
                CompanyPostQueryRepository.SectionCount::getSectionCount));
  }

  private List<String> matchedSkills(List<String> postSkills, List<String> wantSkills) {
    if (wantSkills.isEmpty()) {
      return List.of();
    }
    List<Pattern> patterns =
        wantSkills.stream()
            .map(
                skill ->
                    Pattern.compile(
                        "(?<![\\p{L}\\p{N}])" + Pattern.quote(skill) + "(?![\\p{L}\\p{N}])",
                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))
            .toList();
    return postSkills.stream()
        .filter(skill -> patterns.stream().anyMatch(pattern -> pattern.matcher(skill).find()))
        .toList();
  }

  private record PostMatch(Post post, boolean personalized) {}
}
