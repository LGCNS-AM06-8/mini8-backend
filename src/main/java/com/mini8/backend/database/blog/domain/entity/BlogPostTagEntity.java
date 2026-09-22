package com.mini8.backend.database.blog.domain.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "blog_post_tag")

public class BlogPostTagEntity {
    private  Long blog_post_id;
    private Long tech_tag_id;
    @Column(nullable = true)
    private Integer tag_rang;
}
