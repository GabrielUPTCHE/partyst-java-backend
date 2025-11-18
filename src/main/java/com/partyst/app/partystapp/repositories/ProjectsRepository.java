package com.partyst.app.partystapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.partyst.app.partystapp.entities.Project;

@Repository
public interface ProjectsRepository  extends JpaRepository<Project, Integer>, JpaSpecificationExecutor<Project>{

    List<Project> findAllByUsersUserId(Integer userId);

    @Query("""
        SELECT p
        FROM Project p
        LEFT JOIN p.skills
        LEFT JOIN p.categories
        LEFT JOIN p.users
    """)
    List<Project> findAllWithFullData();


}
