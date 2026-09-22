package com.mini8.backend.database.blog.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity
@Table(name = "blog_post_section")
public class BlogPostSectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long section_id;   
    
    @Column(nullable = false)
    private Long blog_post_id;     

    @Column(nullable = false)
    private Integer seq;           

    @Column(nullable = false,length = 300)
    private String heading; 
    
    @Column(nullable = true,length = 4)
    private String level;   
    
    @Column(nullable = false,columnDefinition = "MEDIUMTEXT")
    private String content_text;   

    @Column(nullable = false)
    private Integer char_count;  
}
