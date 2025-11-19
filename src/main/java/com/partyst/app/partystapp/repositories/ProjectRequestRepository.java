package com.partyst.app.partystapp.repositories;

import com.partyst.app.partystapp.entities.ProjectRequest;
import com.partyst.app.partystapp.entities.Project;
import com.partyst.app.partystapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRequestRepository extends JpaRepository<ProjectRequest, Integer> {
    
    List<ProjectRequest> findByProjectAndStatus(Project project, String status);
    
    List<ProjectRequest> findByUserAndStatus(User user, String status);
    
    Optional<ProjectRequest> findByProjectAndUserAndStatus(Project project, User user, String status);
    
    boolean existsByProjectAndUserAndStatus(Project project, User user, String status);
}
