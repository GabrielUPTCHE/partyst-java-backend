package com.partyst.app.partystapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects", schema = "partyst_plastic")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Integer projectId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name="user_creator_id")
    private Integer userCreatorId;
    
    private Boolean active;

    @ManyToMany
    @JoinTable(
            name = "colaborations",
            schema = "partyst_plastic",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> users;

   @ManyToMany(mappedBy = "projects")
    @JsonIgnore
    private Set<Category> projectsCategory;

   @ManyToMany(mappedBy = "projects")
    @JsonIgnore
    private Set<Category> categories;

    @ManyToMany
    @JoinTable(
        name = "project_skills",
        schema = "partyst_plastic",
        joinColumns = @JoinColumn(name = "project_id"),
        inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<Skill> skills;



}
