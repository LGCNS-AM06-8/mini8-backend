package com.mini8.backend.database.company.domain.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "company")

public class CompanyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long company_id;

    @Column(nullable = false,length = 100)
    private String name;

    @Column(nullable = true,length = 200)
    private String summary;   

    @Column(nullable = true,length = 300)
    private String main_business;   

    @Column(nullable = true,length = 255)
    private String source_url; 

    @Column(nullable = true)
    private LocalDateTime checked_at;      

    @Column(nullable = false,length = 255)
    private String feed_url;    

    @Column(nullable = false,length = 20)
    private String feed_type;
}
