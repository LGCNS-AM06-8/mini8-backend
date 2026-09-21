package com.mini8.backend.database.TechBlog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mini8.backend.database.TechBlog.domain.entity.TechBlogEntity;



public interface TechBlogRepository extends JpaRepository<TechBlogEntity, Integer> {

}