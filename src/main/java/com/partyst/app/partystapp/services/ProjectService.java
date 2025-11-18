package com.partyst.app.partystapp.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.config.ProjectSpecifications;
import com.partyst.app.partystapp.entities.Category;
import com.partyst.app.partystapp.entities.Project;
import com.partyst.app.partystapp.entities.Skill;
import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.dto.ProjectFullRowDto;
import com.partyst.app.partystapp.records.requests.FilterProjectRequest;
import com.partyst.app.partystapp.records.responses.ProjectResponse;
import com.partyst.app.partystapp.repositories.ProjectsRepository;

@Service
public class ProjectService {


    @Autowired
    private ProjectsRepository projectsRepository;

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

}
