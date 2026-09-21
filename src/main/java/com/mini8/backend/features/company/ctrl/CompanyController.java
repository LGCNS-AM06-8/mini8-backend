package com.mini8.backend.features.company.ctrl;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mini8.backend.features.company.service.CompanyService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {
    
    private final CompanyService companyService;

    // getCompanyList: 기업 목록 불러오기
    @GetMapping("/")
    public ResponseEntity<?> getCompanyList() {
        return null;
    }

    // getCompanyPostList: 특정 기업의 포스트 목록 불러오기
    @GetMapping("/{id}/posts")
    public ResponseEntity<?> getCompanyPostList(@PathVariable("id") int id) {
        return null;
    }

    // getCompanyDetail: 기업 상세 정보 불러오기
    @GetMapping("/{id}")
    public ResponseEntity<?> getCompanyDetail(@PathVariable("id") int id) {
        return null;
    }
    
    
    
}
