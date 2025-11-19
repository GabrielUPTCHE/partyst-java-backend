package com.partyst.app.partystapp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partyst.app.partystapp.entities.Task;
import com.partyst.app.partystapp.records.GenericResponse;
import com.partyst.app.partystapp.records.requests.CreateTaskRequest;
import com.partyst.app.partystapp.records.requests.DeleteTaskRequest;
import com.partyst.app.partystapp.records.requests.TaskByUserProjectRequest;
import com.partyst.app.partystapp.records.requests.UpdateTaskRequest;
import com.partyst.app.partystapp.records.requests.UpdateTaskStateRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.TaskResponse;
import com.partyst.app.partystapp.services.TaskService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/byUserProject")
    public ResponseEntity<GenericResponse> byUserProject( @RequestBody TaskByUserProjectRequest entity) {
        Task taskFinded = taskService.getTaskByProjectidTaskId( entity.taskId());
        return ResponseEntity.ok(new GenericResponse<Task>(201, "Tareas encontradas", taskFinded));
    }
    /*
    @PostMapping("/AllbyUserProject")
    public ResponseEntity<GenericResponse> byUserProjectTwo( @RequestBody TaskByUserProjectRequest entity) {
        List<Task> taskFinded = taskService.getTasksByProjectidTaskId( entity.taskId(), entity.projectId());
        return ResponseEntity.ok(new GenericResponse<List<Task>>(201, "Tareas encontradas", taskFinded));
    }*/
    @PostMapping("/create")
    public ResponseEntity<GenericResponse> createTask(@RequestBody CreateTaskRequest entity) {
        System.out.println("🎯 [CONTROLLER] POST /task/create");
        TaskResponse resultCreated = taskService.createTask(entity);
        return ResponseEntity.ok(new GenericResponse<TaskResponse>(201, "Tarea creada", resultCreated));
    }

    @PutMapping("/update")
    public ResponseEntity<GenericResponse> uptadeTask(@RequestBody UpdateTaskRequest entity) {
        System.out.println("🎯 [CONTROLLER] PUT /task/update");
        TaskResponse resultUpdated = taskService.updateTask(entity);
        return ResponseEntity.ok(new GenericResponse<TaskResponse>(200, "Tarea actualizada", resultUpdated));
    }

    @PutMapping("/state")
    public ResponseEntity<GenericResponse> updateTaskState(@RequestBody UpdateTaskStateRequest request) {
        System.out.println("🎯 [CONTROLLER] PUT /task/state");
        TaskResponse resultUpdated = taskService.updateTaskState(request);
        return ResponseEntity.ok(new GenericResponse<TaskResponse>(200, "Estado actualizado", resultUpdated));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<GenericResponse> deleteTask(@RequestBody DeleteTaskRequest request){
        System.out.println("🎯 [CONTROLLER] DELETE /task/delete - taskId: " + request.taskId());
        CreateProjectResponse resultDeleted = taskService.deleteTask(request);
        return ResponseEntity.ok(new GenericResponse<CreateProjectResponse>(200, "Tarea eliminada", resultDeleted));
    }

}
