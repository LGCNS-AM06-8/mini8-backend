package com.mini8.backend.database.TechBlog.domain.entity;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "ARTICLE")

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TechBlogEntity {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private String company;

    private String external_id ;

    private String title;

    private String url;

    private LocalDate published_at;

    private Integer char_count;
    
    private Integer heading_count;
    //private  ??  heading;

    @OneToMany(mappedBy = "blog", orphanRemoval = false)
    private List<TechBlogCategoryEntity> source_categories = new ArrayList<>();

    private Boolean has_code;

    @Column(columnDefinition = "LONGTEXT")
    private String body_html;

    @Column(columnDefinition = "LONGTEXT")
    private String body_text;
    

    
}