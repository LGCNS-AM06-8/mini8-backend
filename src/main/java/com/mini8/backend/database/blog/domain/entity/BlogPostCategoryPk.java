package com.mini8.backend.database.blog.domain.entity;



import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BlogPostCategoryPk implements Serializable {
    // 복합 pk 때문에 Entity에서 분리
    private Long blog_post_id;

    private String name;
}