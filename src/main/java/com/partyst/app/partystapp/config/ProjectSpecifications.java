package com.partyst.app.partystapp.config;


import com.partyst.app.partystapp.entities.Project;
import com.partyst.app.partystapp.entities.Skill;

import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProjectSpecifications {

    public static Specification<Project> hasTitle(String title) {
        return (root, query, builder) ->
                title == null || title.isEmpty()
                        ? null
                        : builder.like(builder.lower(root.get("name")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Project> hasCategory(String category) {
        return (root, query, builder) -> {
            if (category == null || category.isEmpty()) return null;
            Join<Object, Object> categories = root.join("categories");
            return builder.equal(builder.lower(categories.get("name")), category.toLowerCase());
        };
    }

    public static Specification<Project> hasType(String type) {
        return (root, query, builder) ->
                type == null || type.isEmpty()
                        ? null
                        : builder.equal(root.get("type"), type);
    }

    public static Specification<Project> hasSkills(List<Skill> skills) {
        return (root, query, builder) -> {
            if (skills == null || skills.size() == 0) return null;
            query.distinct(true); 
            Join<Object, Object> skillJoin = root.join("skills");
            return skillJoin.get("name").in(skills);
        };
    }

    public static Specification<Project> isActive(Boolean active) {
        return (root, query, builder) ->
                active == null
                        ? null
                        : builder.equal(root.get("active"), active);
    }
}
