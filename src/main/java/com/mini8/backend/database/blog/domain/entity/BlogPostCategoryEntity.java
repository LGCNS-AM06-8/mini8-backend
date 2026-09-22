package com.mini8.backend.database.blog.domain.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
@Entity
@Table(name = "blog_post_category")
public class BlogPostCategoryEntity {
    private  Long blog_post_id;
    @Column(length = 100)
    private String name;
}
