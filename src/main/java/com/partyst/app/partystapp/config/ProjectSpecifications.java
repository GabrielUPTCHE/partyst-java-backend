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

    /**
     * Filtro por título del proyecto
     */
    public static Specification<Project> hasTitle(String title) {
        return (root, query, builder) ->
                title == null || title.isEmpty()
                        ? builder.conjunction() // Retorna true si no hay filtro
                        : builder.like(builder.lower(root.get("name")), "%" + title.toLowerCase() + "%");
    }

    /**
     * Filtro por categoría del proyecto
     */
    public static Specification<Project> hasCategory(String category) {
        return (root, query, builder) -> {
            if (category == null || category.isEmpty()) {
                return builder.conjunction(); // Retorna true si no hay filtro
            }
            Join<Object, Object> categoryJoin = root.join("category");
            return builder.equal(builder.lower(categoryJoin.get("name")), category.toLowerCase());
        };
    }

    /**
     * Filtro por habilidades del proyecto
     * El proyecto debe tener AL MENOS UNA de las habilidades especificadas
     */
    public static Specification<Project> hasSkills(List<Skill> skills) {
        return (root, query, builder) -> {
            if (skills == null || skills.isEmpty()) {
                return builder.conjunction(); // Retorna true si no hay filtro
            }
            
            query.distinct(true); 
            Join<Project, Skill> skillJoin = root.join("skills");
            
            List<Integer> skillIds = skills.stream()
                .map(Skill::getSkillId)
                .collect(Collectors.toList());
                
            return skillJoin.get("skillId").in(skillIds);
        };
    }

    /**
     * Filtro por estado activo del proyecto
     */
    public static Specification<Project> isActive(Boolean active) {
        return (root, query, builder) ->
                active == null
                        ? builder.conjunction() // Retorna true si no hay filtro
                        : builder.equal(root.get("active"), active);
    }

    /**
     * TIPO: public
     * Retorna TODOS los proyectos activos (de todos los usuarios)
     */
    public static Specification<Project> isPublic() {
        return (root, query, builder) -> {
            // Todos los proyectos activos son públicos
            return builder.isTrue(root.get("active"));
        };
    }

    /**
     * TIPO: profile
     * Retorna SOLO los proyectos que el usuario creó
     */
    public static Specification<Project> hasUserCreator(Integer userId) {
        return (root, query, builder) -> {
            if (userId == null) {
                return builder.conjunction();
            }
            // Proyectos donde el usuario es el creador
            return builder.equal(root.get("userCreatorId"), userId);
        };
    }

    /**
     * TIPO: registered
     * Retorna proyectos donde el usuario es creador O colaborador
     */
    public static Specification<Project> hasUserRegistered(Integer userId) {
        return (root, query, builder) -> {
            if (userId == null) {
                return builder.conjunction();
            }
            
            query.distinct(true);
            
            // Condición 1: El usuario es el creador
            Predicate isCreator = builder.equal(root.get("userCreatorId"), userId);
            
            // Condición 2: El usuario es colaborador
            Join<Project, User> usersJoin = root.join("users");
            Predicate isCollaborator = builder.equal(usersJoin.get("userId"), userId);
            
            // Retorna proyectos donde el usuario es creador O colaborador
            return builder.or(isCreator, isCollaborator);
        };
    }
}