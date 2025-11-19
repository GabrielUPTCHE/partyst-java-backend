package com.partyst.app.partystapp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partyst.app.partystapp.entities.Project;
import com.partyst.app.partystapp.records.GenericResponse;
import com.partyst.app.partystapp.records.requests.AcceptRequestRequest;
import com.partyst.app.partystapp.records.requests.CreateProjectRequest;
import com.partyst.app.partystapp.records.requests.FilterProjectRequest;
import com.partyst.app.partystapp.records.requests.JoinProjectRequest;
import com.partyst.app.partystapp.records.requests.RejectRequestRequest;
import com.partyst.app.partystapp.records.requests.RemoveMemberRequest;
import com.partyst.app.partystapp.records.requests.UpdateProjectRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.JoinProjectResponse;
import com.partyst.app.partystapp.records.responses.ProjectBasicResponse;
import com.partyst.app.partystapp.records.responses.ProjectListWrapperResponse;
import com.partyst.app.partystapp.records.responses.ProjectMembersResponse;
import com.partyst.app.partystapp.records.responses.ProjectRequestsResponse;
import com.partyst.app.partystapp.records.responses.ProjectResponse;
import com.partyst.app.partystapp.services.ProjectMembershipService;
import com.partyst.app.partystapp.services.ProjectService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;





@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectMembershipService membershipService;

    @PostMapping("/update")
    public ResponseEntity<GenericResponse> projectUpdate( @RequestBody UpdateProjectRequest entity) {
        CreateProjectResponse createResult = projectService.updateProject(entity);
        return ResponseEntity.ok(new GenericResponse<CreateProjectResponse>(201, "Proyecto actualizado", createResult));
    }

    
    @GetMapping("/getAll")
    public ResponseEntity<GenericResponse> getAll() {
        List<Project> findedProjects = projectService.getAll();
       return ResponseEntity.ok(new GenericResponse<List<Project>>(201, "Todos los proyectos", findedProjects));
    }
    @GetMapping("/byUser/{userId}")
    public ResponseEntity<GenericResponse> getProjectsByIdUser(@PathVariable Integer userId) {
        List<ProjectResponse> findedProjects = projectService.getProjectsByIdUser(userId);
       return ResponseEntity.ok(new GenericResponse<List<ProjectResponse>>(201, "Proyectos del usuario", findedProjects));
    }
    
    @PostMapping("/filter")
    public ResponseEntity<GenericResponse> filterProjects(@RequestBody FilterProjectRequest filters) {
        List<ProjectBasicResponse> findedProjects = projectService.filterProjects(filters);
        ProjectListWrapperResponse wrapper = new ProjectListWrapperResponse(findedProjects);
        return ResponseEntity.ok(new GenericResponse<ProjectListWrapperResponse>(200, "Proyectos filtrados", wrapper));
    }
    

    @PostMapping("/create")
    public ResponseEntity<GenericResponse> createProject(@RequestBody CreateProjectRequest entity) {
        CreateProjectResponse createResult = projectService.createProject(entity);
        return ResponseEntity.ok(new GenericResponse<CreateProjectResponse>(201, "Proyecto creado", createResult));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<GenericResponse> getByProjectId(@PathVariable Integer projectId) {
        try {
            System.out.println("🎯 [CONTROLLER] GET /projects/" + projectId);
            
            ProjectResponse findedProjects = projectService.getProjectById(projectId);
            
            if (findedProjects == null) {
                System.out.println("⚠️ [CONTROLLER] Proyecto no encontrado");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse<List<ProjectResponse>>(404, "Proyecto no encontrado", new ArrayList<>()));
            }
            
            System.out.println("✅ [CONTROLLER] Retornando proyecto encontrado");
            return ResponseEntity.ok(new GenericResponse<ProjectResponse>(
                200,
                "Proyecto encontrado",
                findedProjects
            ));
            
        } catch (Exception e) {
            System.err.println("❌ [CONTROLLER ERROR] " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GenericResponse<List<ProjectResponse>>(500, "Error interno del servidor", new ArrayList<>()));
        }
    }
    
    @DeleteMapping("/{projectId}/delete")
    public ResponseEntity<GenericResponse> deleteeProject(@PathVariable Integer projectId) {
        System.out.println("🎯 [CONTROLLER] DELETE /projects/" + projectId + "/delete");
        CreateProjectResponse findedProjects = projectService.deleteProject(projectId);
        return ResponseEntity.ok(new GenericResponse<CreateProjectResponse>(200, "Proyecto eliminado", findedProjects));
    }

    // ==================== PROJECT MEMBERSHIP ENDPOINTS ====================

    /**
     * Solicitar unirse a un proyecto
     * POST /projects/join
     */
    @PostMapping("/join")
    public ResponseEntity<GenericResponse<JoinProjectResponse>> joinProject(@RequestBody JoinProjectRequest request) {
        try {
            System.out.println("🎯 [CONTROLLER] POST /projects/join - Usuario: " + request.userId() + 
                              ", Proyecto: " + request.projectId());
            
            JoinProjectResponse result = membershipService.joinProject(request);
            
            HttpStatus status = HttpStatus.valueOf(result.code());
            
            return ResponseEntity.status(status)
                .body(new GenericResponse<JoinProjectResponse>(
                    result.code(), 
                    result.message(), 
                    result
                ));
                
        } catch (Exception e) {
            System.err.println("❌ [CONTROLLER ERROR] " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GenericResponse<>(500, "Error interno del servidor", null));
        }
    }

    /**
     * Obtener solicitudes pendientes de un proyecto
     * GET /projects/{projectId}/requests
     */
    @GetMapping("/{projectId}/requests")
    public ResponseEntity<GenericResponse<ProjectRequestsResponse>> getProjectRequests(@PathVariable Integer projectId) {
        System.out.println("🎯 [CONTROLLER] GET /projects/" + projectId + "/requests");
        ProjectRequestsResponse result = membershipService.getProjectRequests(projectId);
        return ResponseEntity.ok(new GenericResponse<ProjectRequestsResponse>(200, result.message(), result));
    }

    /**
     * Aceptar solicitud de un usuario
     * POST /projects/requests/accept
     */
    @PostMapping("/requests/accept")
    public ResponseEntity<GenericResponse<CreateProjectResponse>> acceptRequest(@RequestBody AcceptRequestRequest request) {
        System.out.println("🎯 [CONTROLLER] POST /projects/requests/accept - Proyecto: " + 
                          request.projectid() + ", Usuario: " + request.userid());
        CreateProjectResponse result = membershipService.acceptRequest(request);
        return ResponseEntity.ok(new GenericResponse<CreateProjectResponse>(200, result.message(), result));
    }

    /**
     * Rechazar solicitud de un usuario
     * POST /projects/requests/decline
     */
    @PostMapping("/requests/decline")
    public ResponseEntity<GenericResponse<CreateProjectResponse>> rejectRequest(@RequestBody RejectRequestRequest request) {
        System.out.println("🎯 [CONTROLLER] POST /projects/requests/decline - Proyecto: " + 
                          request.projectid() + ", Usuario: " + request.userid());
        CreateProjectResponse result = membershipService.rejectRequest(request);
        return ResponseEntity.ok(new GenericResponse<CreateProjectResponse>(200, result.message(), result));
    }

    /**
     * Obtener miembros de un proyecto
     * GET /projects/{projectId}/members
     */
    @GetMapping("/{projectId}/members")
    public ResponseEntity<GenericResponse<ProjectMembersResponse>> getProjectMembers(@PathVariable Integer projectId) {
        System.out.println("🎯 [CONTROLLER] GET /projects/" + projectId + "/members");
        ProjectMembersResponse result = membershipService.getProjectMembers(projectId);
        return ResponseEntity.ok(new GenericResponse<ProjectMembersResponse>(200, result.message(), result));
    }

    /**
     * Eliminar miembro de un proyecto
     * DELETE /projects/member
     */
    @DeleteMapping("/member")
    public ResponseEntity<GenericResponse<CreateProjectResponse>> removeMember(@RequestBody RemoveMemberRequest request) {
        System.out.println("🎯 [CONTROLLER] DELETE /projects/member - Proyecto: " + 
                          request.projectid() + ", Usuario: " + request.userid());
        CreateProjectResponse result = membershipService.removeMember(request);
        return ResponseEntity.ok(new GenericResponse<CreateProjectResponse>(200, result.message(), result));
    }

}
