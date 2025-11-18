package com.partyst.app.partystapp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partyst.app.partystapp.entities.Project;
import com.partyst.app.partystapp.records.GenericResponse;
import com.partyst.app.partystapp.records.requests.FilterProjectRequest;
import com.partyst.app.partystapp.records.responses.ProjectResponse;
import com.partyst.app.partystapp.services.ProjectService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

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
    public ResponseEntity<GenericResponse> postMethodName(@RequestBody FilterProjectRequest filters) {
        List<Project> findedProjects = projectService.filterProjects(filters);
        return ResponseEntity.ok(new GenericResponse<List<Project>>(201, "Proyectos filtrados", findedProjects));
    }
    
    

}
