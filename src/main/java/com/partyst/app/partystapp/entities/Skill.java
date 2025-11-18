package com.partyst.app.partystapp.entities;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "skills", schema = "partyst_plastic")
public class Skill {

    @Id
    @GeneratedValue
    @Column(name = "skill_id")
    private Integer skillId;

    private String name;

    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private Set<User> users;

    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private Set<Project> projects;
}

