package com.partyst.app.partystapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.entities.Task;
import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.requests.CreateTaskRequest;
import com.partyst.app.partystapp.records.requests.DeleteTaskRequest;
import com.partyst.app.partystapp.records.requests.UpdateTaskRequest;
import com.partyst.app.partystapp.records.requests.UpdateTaskStateRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.TaskResponse;
import com.partyst.app.partystapp.records.responses.UserBasicInfo;
import com.partyst.app.partystapp.repositories.TaskRepository;
import com.partyst.app.partystapp.repositories.UserRepository;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public Task getTaskByProjectidTaskId(Integer taskId) {
        Task taskFinded = taskRepository.findByTaskId(taskId).orElseThrow();
        return taskFinded;
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        User user = userRepository.findByEmail(request.assignedUserEmail())
                .orElseThrow(() -> {
                    System.err.println("Usuario no encontrado con email: " + request.assignedUserEmail());
                    return new IllegalArgumentException(
                            "Usuario no encontrado con email: " + request.assignedUserEmail());
                });


        Task newTask = Task.builder()
                .assignedUser(user)
                .name(request.name())
                .state(request.state() != null ? request.state() : "to be done")
                .projectId(request.projectId())
                .build();

        Task savedTask = taskRepository.save(newTask);
        return convertToTaskResponse(savedTask);
    }

    public TaskResponse updateTask(UpdateTaskRequest request) {

        Task findedTask = taskRepository.findByTaskId(request.taskId())
                .orElseThrow(() -> {
                    return new IllegalArgumentException("Tarea no encontrada con ID: " + request.taskId());
                });

        User findedUser = null;
        if (request.assignedUserEmail() != null && !request.assignedUserEmail().isEmpty()) {
            findedUser = userRepository.findByEmail(request.assignedUserEmail())
                    .orElseThrow(() -> {
                        return new IllegalArgumentException(
                                "Usuario no encontrado con email: " + request.assignedUserEmail());
                    });
            findedTask.setAssignedUser(findedUser);
        }

        if (request.name() != null && !request.name().isEmpty()) {
            findedTask.setName(request.name());
        }

        if (request.state() != null && !request.state().isEmpty()) {
            findedTask.setState(request.state());
        }

        Task updatedTask = taskRepository.save(findedTask);
        return convertToTaskResponse(updatedTask);
    }

    public TaskResponse updateTaskState(UpdateTaskStateRequest request) {
        Task task = taskRepository.findByTaskId(request.taskId())
                .orElseThrow(() -> {
                    return new IllegalArgumentException("Tarea no encontrada con ID: " + request.taskId());
                });

        task.setState(request.state());

        Task updatedTask = taskRepository.save(task);
        return convertToTaskResponse(updatedTask);
    }

    public CreateProjectResponse deleteTask(DeleteTaskRequest request) {
        Task task = taskRepository.findByTaskId(request.taskId()).orElse(null);
        if (task != null) {
            taskRepository.delete(task);
            return new CreateProjectResponse(true, "Se eliminó la tarea");
        }

        return new CreateProjectResponse(false, "Error al eliminar la tarea");
    }

    private TaskResponse convertToTaskResponse(Task task) {
        UserBasicInfo userInfo = null;

        if (task.getAssignedUser() != null) {
            User user = task.getAssignedUser();
            userInfo = new UserBasicInfo(
                    user.getUserId(),
                    user.getName(),
                    user.getLastname(),
                    user.getEmail(),
                    user.getNickname());
        }

        return new TaskResponse(
                task.getTaskId(),
                task.getName(),
                task.getState(),
                task.getProjectId(),
                userInfo);
    }
}
