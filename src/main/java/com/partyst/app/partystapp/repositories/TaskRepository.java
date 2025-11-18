package com.partyst.app.partystapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.partyst.app.partystapp.entities.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

}
