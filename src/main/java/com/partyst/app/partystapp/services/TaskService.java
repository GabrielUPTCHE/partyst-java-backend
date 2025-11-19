package com.partyst.app.partystapp.services;

import java.util.List;

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
    
    /*public List<Task> getTasksByProjectidTaskId(Integer taskId, Integer projectId){
        List<Task> taskFinded = taskRepositoryTwo.findTasks( taskId, projectId);
        return taskFinded;
    }*/

    public TaskResponse createTask(CreateTaskRequest request) {
        System.out.println("🆕 [TASK CREATE] Creando nueva tarea...");
        System.out.println("📥 Request: projectId=" + request.projectId() + 
                          ", name=" + request.name() + 
                          ", state=" + request.state() +
                          ", email=" + request.assignedUserEmail());

        User user = userRepository.findByEmail(request.assignedUserEmail())
            .orElseThrow(() -> {
                System.err.println("⚠️ Usuario no encontrado con email: " + request.assignedUserEmail());
                return new IllegalArgumentException("Usuario no encontrado con email: " + request.assignedUserEmail());
            });

        System.out.println("✅ Usuario encontrado: " + user.getName() + " " + user.getLastname());

        Task newTask = Task.builder()
            .assignedUser(user)
            .name(request.name())
            .state(request.state() != null ? request.state() : "to be done")
            .projectId(request.projectId())
            .build();

        System.out.println("💾 Guardando tarea...");
        Task savedTask = taskRepository.save(newTask);
        System.out.println("✅ Tarea guardada con ID: " + savedTask.getTaskId());

        return convertToTaskResponse(savedTask);
    }

    public TaskResponse updateTask(UpdateTaskRequest request) {
        System.out.println("🔄 [TASK UPDATE] Actualizando tarea...");
        System.out.println("📥 Request: taskId=" + request.taskId() + 
                          ", name=" + request.name() + 
                          ", state=" + request.state() +
                          ", email=" + request.assignedUserEmail());

        Task findedTask = taskRepository.findByTaskId(request.taskId())
            .orElseThrow(() -> {
                System.err.println("❌ Tarea no encontrada con ID: " + request.taskId());
                return new IllegalArgumentException("Tarea no encontrada con ID: " + request.taskId());
            });

        System.out.println("✅ Tarea encontrada: " + findedTask.getName());

        User findedUser = null;
        if (request.assignedUserEmail() != null && !request.assignedUserEmail().isEmpty()) {
            findedUser = userRepository.findByEmail(request.assignedUserEmail())
                .orElseThrow(() -> {
                    System.err.println("⚠️ Usuario no encontrado con email: " + request.assignedUserEmail());
                    return new IllegalArgumentException("Usuario no encontrado con email: " + request.assignedUserEmail());
                });
            
            System.out.println("🔄 Actualizando usuario asignado → " + findedUser.getEmail());
            findedTask.setAssignedUser(findedUser);
        }
        
        if (request.name() != null && !request.name().isEmpty()) {
            System.out.println("🔄 Actualizando nombre: '" + findedTask.getName() + "' → '" + request.name() + "'");
            findedTask.setName(request.name());
        }

        if (request.state() != null && !request.state().isEmpty()) {
            System.out.println("🔄 Actualizando estado: '" + findedTask.getState() + "' → '" + request.state() + "'");
            findedTask.setState(request.state());
        }

        System.out.println("💾 Guardando cambios...");
        Task updatedTask = taskRepository.save(findedTask);
        System.out.println("✅ Tarea actualizada correctamente");

        return convertToTaskResponse(updatedTask);
    }

    /**
     * Actualizar solo el estado de una tarea (usado en drag & drop)
     */
    public TaskResponse updateTaskState(UpdateTaskStateRequest request) {
        System.out.println("🔄 [TASK STATE UPDATE] Actualizando estado de tarea...");
        System.out.println("📥 Request: taskId=" + request.taskId() + ", state=" + request.state());

        Task task = taskRepository.findByTaskId(request.taskId())
            .orElseThrow(() -> {
                System.err.println("❌ Tarea no encontrada con ID: " + request.taskId());
                return new IllegalArgumentException("Tarea no encontrada con ID: " + request.taskId());
            });

        System.out.println("🔄 Cambiando estado: '" + task.getState() + "' → '" + request.state() + "'");
        task.setState(request.state());

        Task updatedTask = taskRepository.save(task);
        System.out.println("✅ Estado actualizado correctamente");

        return convertToTaskResponse(updatedTask);
    }

    public CreateProjectResponse deleteTask(DeleteTaskRequest request) {
        System.out.println("🗑️ [TASK DELETE] Eliminando tarea...");
        System.out.println("📥 Request: taskId=" + request.taskId() + ", projectId=" + request.projectId());
        
        Task task = taskRepository.findByTaskId(request.taskId()).orElse(null);
        if (task != null) {
            System.out.println("✅ Tarea encontrada: " + task.getName());
            taskRepository.delete(task);
            System.out.println("✅ Tarea eliminada exitosamente");
            return new CreateProjectResponse(true, "Se eliminó la tarea");
        }
        
        System.err.println("❌ Tarea no encontrada con ID: " + request.taskId());
        return new CreateProjectResponse(false, "Error al eliminar la tarea");
    }

    /**
     * Convertir Task entity a TaskResponse DTO con información del usuario
     */
    private TaskResponse convertToTaskResponse(Task task) {
        UserBasicInfo userInfo = null;
        
        if (task.getAssignedUser() != null) {
            User user = task.getAssignedUser();
            userInfo = new UserBasicInfo(
                user.getUserId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getNickname()
            );
        }

        return new TaskResponse(
            task.getTaskId(),
            task.getName(),
            task.getState(),
            task.getProjectId(),
            userInfo
        );
    }
}
