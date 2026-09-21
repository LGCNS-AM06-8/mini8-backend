package com.mini8.backend.database.TechBlog.domain.dto;


import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor


public class TechBlogRequestDTO {
    
    private String company,external_id,title,content,url;
    private LocalDate published_at;
    private Integer char_count,heading_count;
    //private List<String> headings;
    private List<String> source_categories;
    private Boolean has_code;
    private String body_html,body_text;
}
