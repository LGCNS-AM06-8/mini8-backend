package com.mini8.backend.database.TechBlog.controller;

import org.springframework.web.bind.annotation.*;

import com.mini8.backend.database.TechBlog.domain.dto.TechBlogRequestDTO;
import com.mini8.backend.database.TechBlog.service.TechBlogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/TechBlog")
@RequiredArgsConstructor
public class TechBlogController {

    private final TechBlogService techBlogService;

    @PostMapping
    public String create(
            @RequestBody TechBlogRequestDTO request
    ) {

        System.out.println("===== 크롤링 데이터 확인 =====");
        System.out.println(request);
        System.out.println("============================");

        techBlogService.save(request);

        return "DB 저장 완료";
    }
}