package com.partyst.app.partystapp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Locale.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.config.ProjectSpecifications;
import com.partyst.app.partystapp.entities.Project;
import com.partyst.app.partystapp.records.requests.CreateProjectRequest;
import com.partyst.app.partystapp.records.requests.FilterProjectRequest;
import com.partyst.app.partystapp.records.requests.UpdateProjectRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.ProjectResponse;
import com.partyst.app.partystapp.repositories.CategoryRepository;
import com.partyst.app.partystapp.repositories.ProjectsRepository;

@Service
public class ProjectService {


    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private CategoryRepository categoryRepository;


    public List<ProjectResponse> getProjectsByIdUser(Integer userId){
      List<Project> findedProjects = projectsRepository.findAllByUsersUserId(userId);
       return listDtoProjects(findedProjects);
    }

    public List<Project> getAll(){
      List<Project> findedProjects = projectsRepository.findAllWithFullData();
       return findedProjects;
    }


    public List<Project> filterProjects(FilterProjectRequest filters) {
      Specification<Project> spec =
                Specification.where(ProjectSpecifications.hasTitle(filters.title()))
                        .and(ProjectSpecifications.hasCategory(filters.category()))
                        .and(ProjectSpecifications.hasType(filters.type()))
                        .and(ProjectSpecifications.hasSkills(filters.skills()))
                        .and(ProjectSpecifications.isActive(filters.active()));

        return projectsRepository.findAll(spec);
    }

    public List<ProjectResponse> getByProjectId(Integer projectId) {
      List<Project> findedList = projectsRepository.findAllByProjectId(projectId);
      return listDtoProjects(findedList);
    }

    public CreateProjectResponse updateProject(UpdateProjectRequest request){
      Project updatedProject = projectsRepository.findById(request.projectId()).orElse(null);
      if (updatedProject != null) {
        updatedProject.setDescription(request.description());
        updatedProject.setName(request.title());
        updatedProject.setSkills(request.skills());
        projectsRepository.save(updatedProject);
        return new CreateProjectResponse(true, "Se actualizo el proyecto");
      }
      return new CreateProjectResponse(false, "Fallo la actualizacion");
      
    }

    public CreateProjectResponse deleteProject(Integer projectId){
      Project updatedProject = projectsRepository.findById(projectId).orElse(null);
      if (updatedProject != null) {

        projectsRepository.deleteById(projectId);
        return new CreateProjectResponse(true, "Se actualizo el proyecto");
      }
      return new CreateProjectResponse(false, "Fallo la actualizacion");
      
    }

    private List<ProjectResponse> listDtoProjects(List<Project> products){
      List<ProjectResponse> projecsResponse = new ArrayList<>();
      for (Project finded : products) {
         projecsResponse.add(new ProjectResponse(
            finded.getProjectId(), 
            finded.getName(),
            finded.getDescription(), 
            finded.getUsers(), 
            finded.getCategories(), 
            finded.getSkills()));
      }
      return projecsResponse;
    }

    public CreateProjectResponse createProject(CreateProjectRequest request){
      Project newProject = Project.builder()
                            .name(request.title())
                            .userCreatorId(request.userId())
                            .skills(request.skills())
                            .build();
      Project projectSaved = projectsRepository.save(newProject);
      return new CreateProjectResponse(true, "Se creo correctamente el proyecto");
    }

}
