package com.mini8.backend.database.User.controller;
import org.springframework.web.bind.annotation.*;

import com.mini8.backend.database.TechBlog.domain.dto.TechBlogRequestDTO;
import com.mini8.backend.database.TechBlog.service.TechBlogService;

import lombok.RequiredArgsConstructor;

import com.mini8.backend.database.User.domain.dto.UserRequestDTO;
import com.mini8.backend.database.User.service.UserService;
@RestController
@RequestMapping("/User")
@RequiredArgsConstructor
public class UserController {
    private final UserService userservice;

    @PostMapping
    public String create(
            @RequestBody UserRequestDTO request
    ) {

        System.out.println("===== 사용자 데이터 확인 =====");
        System.out.println(request);
        System.out.println("============================");

        userservice.save(request);

        return "사용자 데이터 DB 저장 완료";
    }

}
