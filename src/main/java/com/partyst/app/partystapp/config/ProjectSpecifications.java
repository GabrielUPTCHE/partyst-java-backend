package com.partyst.app.partystapp.config;

import com.partyst.app.partystapp.entities.Project;
import com.partyst.app.partystapp.entities.Skill;
import com.partyst.app.partystapp.entities.User;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;

public class ProjectSpecifications {

    public static Specification<Project> hasTitle(String title) {
        return (root, query, builder) ->
                title == null || title.isEmpty()
                        ? builder.conjunction()
                        : builder.like(builder.lower(root.get("name")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Project> hasCategory(String category) {
        return (root, query, builder) -> {
            if (category == null || category.isEmpty()) {
                return builder.conjunction();
            }
            Join<Object, Object> categoryJoin = root.join("category");
            return builder.equal(builder.lower(categoryJoin.get("name")), category.toLowerCase());
        };
    }

    public static Specification<Project> hasSkills(List<Skill> skills) {
        return (root, query, builder) -> {
            if (skills == null || skills.isEmpty()) {
                return builder.conjunction(); 
            }
            
            query.distinct(true); 
            Join<Project, Skill> skillJoin = root.join("skills");
            
            List<Integer> skillIds = skills.stream()
                .map(Skill::getSkillId)
                .collect(Collectors.toList());
                
            return skillJoin.get("skillId").in(skillIds);
        };
    }

    public static Specification<Project> isActive(Boolean active) {
        return (root, query, builder) ->
                active == null
                        ? builder.conjunction()
                        : builder.equal(root.get("active"), active);
    }

    public static Specification<Project> isPublic() {
        return (root, query, builder) -> {
            return builder.isTrue(root.get("active"));
        };
    }

    public static Specification<Project> hasUserCreator(Integer userId) {
        return (root, query, builder) -> {
            if (userId == null) {
                return builder.conjunction();
            }
            return builder.equal(root.get("userCreatorId"), userId);
        };
    }

    public static Specification<Project> hasUserRegistered(Integer userId) {
        return (root, query, builder) -> {
            if (userId == null) {
                return builder.conjunction();
            }
            
            query.distinct(true);
            
            Predicate isCreator = builder.equal(root.get("userCreatorId"), userId);
            
            Join<Project, User> usersJoin = root.join("users");
            Predicate isCollaborator = builder.equal(usersJoin.get("userId"), userId);
            
            return builder.or(isCreator, isCollaborator);
        };
    }
}