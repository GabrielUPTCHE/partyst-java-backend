package com.partyst.app.partystapp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.partyst.app.partystapp.entities.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    Optional<Task> findByTaskId(Integer taskId);
    
    List<Task> findByProjectId(Integer projectId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Task t WHERE t.projectId = :projectId")
    void deleteByProjectId(@Param("projectId") Integer projectId);

}
