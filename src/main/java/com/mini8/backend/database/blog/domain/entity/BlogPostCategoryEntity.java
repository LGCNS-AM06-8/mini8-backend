package com.mini8.backend.database.blog.domain.entity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "blog_post_category")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostCategoryEntity {
    @EmbeddedId
    private BlogPostCategoryPk id;
}
