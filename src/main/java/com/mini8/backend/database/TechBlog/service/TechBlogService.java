package com.mini8.backend.database.TechBlog.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mini8.backend.database.TechBlog.domain.dto.TechBlogRequestDTO;
import com.mini8.backend.database.TechBlog.domain.entity.TechBlogCategoryEntity;
import com.mini8.backend.database.TechBlog.domain.entity.TechBlogEntity;
import com.mini8.backend.database.TechBlog.repository.TechBlogCategoryRepository;
import com.mini8.backend.database.TechBlog.repository.TechBlogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TechBlogService {

    private final TechBlogRepository techBlogRepository;
    private final TechBlogCategoryRepository techBlogCategoryRepository;

    @Transactional
    public void save(TechBlogRequestDTO request) {

        // 1. 게시글 저장
        TechBlogEntity techBlog = TechBlogEntity.builder()
                .company(request.getCompany())
                .external_id(request.getExternal_id())
                .url(request.getUrl())
                .published_at(request.getPublished_at())
                .char_count(request.getChar_count())
                .heading_count(request.getHeading_count())
                .has_code(request.getHas_code())
                .body_html(request.getBody_html())
                .body_text(request.getBody_text())
                .build();

        TechBlogEntity savedBlog = techBlogRepository.save(techBlog);

        // 2. 카테고리 저장
        if (request.getSource_categories() != null) {

            for (String category : request.getSource_categories()) {

                TechBlogCategoryEntity categoryEntity =
                        TechBlogCategoryEntity.builder()
                                .category(category)
                                .blog(savedBlog)
                                .build();

                techBlogCategoryRepository.save(categoryEntity);
            }
        }
    }
}