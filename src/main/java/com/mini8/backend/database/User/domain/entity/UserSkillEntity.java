package com.mini8.backend.database.User.domain.entity;

import com.mini8.backend.database.Tech.domain.entity.TechTagEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USER_SKILL")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSkillEntity {

    @EmbeddedId
    private UserSkillId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("techTagId")
    @JoinColumn(name = "tech_tag_id")
    private TechTagEntity techTag;

    @Column(name = "skill_type", nullable = false, length = 10)
    private String skill_type;
}