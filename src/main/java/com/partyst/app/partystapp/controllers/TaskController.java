package com.partyst.app.partystapp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partyst.app.partystapp.entities.Task;
import com.partyst.app.partystapp.records.GenericResponse;
import com.partyst.app.partystapp.records.requests.TaskByUserProjectRequest;
import com.partyst.app.partystapp.services.TaskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/byUserProject")
    public ResponseEntity<GenericResponse> byUserProject( @RequestBody TaskByUserProjectRequest entity) {
        Task taskFinded = taskService.getTaskByProjectidTaskId( entity.taskId());
        return ResponseEntity.ok(new GenericResponse<Task>(201, "Proyecto actualizado", taskFinded));
    }
    

}
