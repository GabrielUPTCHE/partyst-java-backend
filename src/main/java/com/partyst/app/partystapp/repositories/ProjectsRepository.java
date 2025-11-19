package com.partyst.app.partystapp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.partyst.app.partystapp.entities.Project;

import jakarta.transaction.Transactional;

@Repository
public interface ProjectsRepository  extends JpaRepository<Project, Integer>, JpaSpecificationExecutor<Project>{

    List<Project> findAllByUsersUserId(Integer userId);

    @Query("""
        SELECT DISTINCT p 
        FROM Project p 
        LEFT JOIN FETCH p.category 
        LEFT JOIN FETCH p.skills 
        LEFT JOIN FETCH p.users 
        WHERE p.projectId = :projectId
    """)
    List<Project> findByProjectId(@Param("projectId") Integer projectId);

    @Query("""
        SELECT p
        FROM Project p
        LEFT JOIN p.skills
        LEFT JOIN p.category
        LEFT JOIN p.users
    """)
    List<Project> findAllWithFullData();



}
