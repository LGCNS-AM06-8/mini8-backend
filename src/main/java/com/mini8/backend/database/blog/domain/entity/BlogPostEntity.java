package com.mini8.backend.database.blog.domain.entity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
@Entity
@Table(name = "blog_post")
public class BlogPostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blog_post_id;

    @Column(nullable = false)
    private Long company_id;

    @Column(nullable = true,length = 255)
    private String external_id ;

    @Column(nullable = false, unique = true,length = 500)
    private String url;

    @Column(nullable = false,length = 300)
    private String title;

    @Column(nullable = false)
    private LocalDateTime published_at;

    @Column(columnDefinition = "MEDIUMTEXT",nullable = false)
    private String content_html;

    @Column(columnDefinition = "MEDIUMTEXT",nullable = false)
    private String content_text;

    @Column(nullable = false)
    private Integer char_count;

    @Column(nullable = false)
    private Boolean has_code;

    @Column(nullable = false)
    private LocalDateTime collected_at;

    @Column(nullable = true)
    private Boolean is_tech;

    @Column(nullable = true,length = 10)
    private String field;

    @Column(nullable = true,length = 4)
    private String level;
    
    @Column(nullable = true,length = 300)
    private String summary;
}
