package com.partyst.app.partystapp.entities;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories", schema = "partyst_plastic")
public class Category {
    @Id
    @GeneratedValue
    @Column(name = "categories_id")
    private Integer categoryId;

    private String name;
    private String description;

    @ManyToMany
    @JoinTable(
        name = "project_categories",
        schema = "partyst_plastic",
        joinColumns = @JoinColumn(name = "categorie_id"),      // Category → category_id
        inverseJoinColumns = @JoinColumn(name = "project_id")  // Project → project_id
    )
    @JsonIgnore
    private Set<Project> projects;

    
    
    


}
