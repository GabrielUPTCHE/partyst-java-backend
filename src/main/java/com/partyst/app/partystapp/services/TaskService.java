package com.partyst.app.partystapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.entities.Task;
import com.partyst.app.partystapp.repositories.TaskRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task getTaskByProjectidTaskId(Integer taskId){
        Task taskFinded = taskRepository.findByTaskId( taskId).orElseThrow();
        return taskFinded;
    }

}
