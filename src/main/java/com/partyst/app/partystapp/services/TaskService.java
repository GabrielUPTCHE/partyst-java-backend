package com.partyst.app.partystapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.entities.Task;
import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.requests.CreateTaskRequest;
import com.partyst.app.partystapp.records.requests.DeleteTaskRequest;
import com.partyst.app.partystapp.records.requests.UpdateTaskRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.repositories.TaskJdbcRepository;
import com.partyst.app.partystapp.repositories.TaskRepository;
import com.partyst.app.partystapp.repositories.UserRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskJdbcRepository taskRepositoryTwo;

    @Autowired
    private UserRepository userRepository;
    

    public Task getTaskByProjectidTaskId(Integer taskId){
        Task taskFinded = taskRepository.findByTaskId( taskId).orElseThrow();
        return taskFinded;
    }
    
    public List<Task> getTasksByProjectidTaskId(Integer taskId, Integer projectId){
        List<Task> taskFinded = taskRepositoryTwo.findTasks( taskId, projectId);
        return taskFinded;
    }

    public CreateProjectResponse  createTask(CreateTaskRequest request) {
        User user = userRepository.findByEmail(request.email()).orElse(null);
        if (user != null) {
            taskRepository.save(Task.builder()
                                    .assignedUser(user)
                                    .name(request.name())
                                    .state("Por hacer")
                                    .projectId(request.projectId())
                                    .build());
            return new CreateProjectResponse(true, "Se creo la tarea");
        }
        return new CreateProjectResponse(false, "Error al crear la tarea");
    }

    public CreateProjectResponse updateTask(UpdateTaskRequest request) {

        Task findedTask = taskRepository.findById(request.taskId()).orElse(null);
         User findedUser = null;
        if (request.email() != null) {
            findedUser = userRepository.findByEmail(request.email()).orElse(null);
        }     
        
        if (findedUser != null) {
            findedTask.setAssignedUser(findedUser);
        }
        
        if (findedTask != null) {
            findedTask.setName(request.name());
            findedTask.setState(request.state());
            taskRepository.save(findedTask);
            return new CreateProjectResponse(true, "Se edito la tarea");
        }
        return new CreateProjectResponse(false, "Error al editar la tarea");
    }

    public CreateProjectResponse deleteTask(DeleteTaskRequest request) {
        Task task = taskRepository.findById(request.taskId()).orElse(null);
        if (task != null) {
            taskRepository.delete(task);
            return new CreateProjectResponse(true, "Se elimino la tarea");
        }
        return new CreateProjectResponse(false, "Error al eliminar la tarea");
    }
}
