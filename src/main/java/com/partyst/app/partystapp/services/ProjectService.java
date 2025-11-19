package com.partyst.app.partystapp.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.config.ProjectSpecifications;
import com.partyst.app.partystapp.entities.Category;
import com.partyst.app.partystapp.entities.Project;
import com.partyst.app.partystapp.entities.Task;
import com.partyst.app.partystapp.records.dtos.SkillBasicDTO;
import com.partyst.app.partystapp.records.dtos.TaskBasicDTO;
import com.partyst.app.partystapp.records.requests.CreateProjectRequest;
import com.partyst.app.partystapp.records.requests.FilterProjectRequest;
import com.partyst.app.partystapp.records.requests.UpdateProjectRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.ProjectBasicResponse;
import com.partyst.app.partystapp.records.responses.ProjectResponse;
import com.partyst.app.partystapp.records.responses.SkillBasicResponse;
import com.partyst.app.partystapp.repositories.CategoryRepository;
import com.partyst.app.partystapp.repositories.ProjectsRepository;
import com.partyst.app.partystapp.repositories.TaskJdbcRepository;

@Service
public class ProjectService {


    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaskJdbcRepository taskJdbcRepository;


    public List<ProjectResponse> getProjectsByIdUser(Integer userId){
      List<Project> findedProjects = projectsRepository.findAllByUsersUserId(userId);
       return listDtoProjects(findedProjects);
    }

    public List<Project> getAll(){
      List<Project> findedProjects = projectsRepository.findAllWithFullData();
       return findedProjects;
    }


    public List<ProjectBasicResponse> filterProjects(FilterProjectRequest filters) {
        
        // Construimos la specification dinámica
        Specification<Project> spec = Specification.where(null);

        // Filtrar por título
        spec = spec.and(ProjectSpecifications.hasTitle(filters.title()));

        // Filtrar por categoría
        spec = spec.and(ProjectSpecifications.hasCategory(filters.category()));

        // Filtrar por skills
        spec = spec.and(ProjectSpecifications.hasSkills(filters.skills()));

        // Filtrar por estado activo
        spec = spec.and(ProjectSpecifications.isActive(filters.active()));

        // Filtro por tipo (CRÍTICO PARA EL DASHBOARD)
        if ("public".equalsIgnoreCase(filters.type())) {
            // TIPO PUBLIC: Todos los proyectos activos
            spec = spec.and(ProjectSpecifications.isPublic());
            
        } else if ("registered".equalsIgnoreCase(filters.type())) {
            // TIPO REGISTERED: Proyectos donde el usuario es creador O colaborador
            spec = spec.and(ProjectSpecifications.hasUserRegistered(filters.userId()));
            
        } else if ("profile".equalsIgnoreCase(filters.type())) {
            // TIPO PROFILE: Solo proyectos que el usuario creó
            spec = spec.and(ProjectSpecifications.hasUserCreator(filters.userId()));
        }

        // Ejecutamos la query filtrada
        List<Project> projects = projectsRepository.findAll(spec);

        // Transformamos al response (ajustado al formato del frontend)
        return projects.stream()
            .map(p -> new ProjectBasicResponse(
                    p.getProjectId(),
                    p.getName(),
                    p.getDescription(),
                    p.getCategory() != null ? p.getCategory().getName() : "Sin categoría",
                    p.getSkills() != null
                        ? p.getSkills().stream()
                            .map(skill -> new SkillBasicResponse(skill.getName()))
                            .toList()
                        : List.of()
            ))
            .toList();
    }



    public ProjectResponse getProjectById(Integer projectId) {
    try {
        System.out.println("🔍 [1] Buscando proyecto ID: " + projectId);
        
        List<Project> projects = projectsRepository.findByProjectId(projectId);
        if (projects.isEmpty()) {
            System.out.println("⚠️ [3] No hay proyectos");
            return null;  // ← Retorna null en lugar de lista vacía
        }
        
        // Tomar solo el primer proyecto (debería ser único por ID)
        Project project = projects.get(0);
        System.out.println("✅ [4] Proyecto encontrado: " + project.getName());
        
        // Convertir el proyecto único a ProjectResponse
        ProjectResponse response = convertToProjectResponse(project);
        System.out.println("🚀 [8] Response único creado");
        
        return response;  // ← Retorna objeto único
        
    } catch (Exception e) {
        System.err.println("❌ [ERROR] En getProjectById: " + e.getMessage());
        e.printStackTrace();
        return null;  // ← Retorna null en caso de error
    }
}

private ProjectResponse convertToProjectResponse(Project project) {
    // Obtener las tareas de este proyecto específico
    Set<Task> tasks = taskJdbcRepository.findTasksByProjectId(project.getProjectId());
    System.out.println("✅ [11] Tareas encontradas: " + tasks.size());
    
    // Convertir Set<Task> a Set<TaskBasicDTO>
    Set<TaskBasicDTO> taskDTOs = tasks.stream()
        .map(task -> new TaskBasicDTO(
            task.getName() != null ? task.getName() : "Sin nombre",
            task.getState() != null ? task.getState() : "to be done",
            task.getAssignedUser() != null ? String.valueOf(task.getAssignedUser().getUserId()) : "Sin usuario"
        ))
        .collect(Collectors.toSet());
    System.out.println("✅ [12] TaskDTOs creados: " + taskDTOs.size());
    
    // Convertir Set<Skill> a Set<SkillBasicDTO>
    Set<SkillBasicDTO> skillDTOs = new HashSet<>();
    if (project.getSkills() != null) {
        skillDTOs = project.getSkills().stream()
            .map(skill -> new SkillBasicDTO(
                skill.getName() != null ? skill.getName() : "Sin nombre"
            ))
            .collect(Collectors.toSet());
    }
    System.out.println("✅ [13] SkillDTOs creados: " + skillDTOs.size());
    
    // Crear y retornar el response único
    return new ProjectResponse(
        project.getProjectId(), 
        project.getName(),
        project.getDescription(), 
        project.getUsers(),  // ← users vacío para evitar problemas de serialización
        getCategoryName(project.getCategory()),
        skillDTOs,
        taskDTOs
    );
}

    public CreateProjectResponse updateProject(UpdateProjectRequest request){
    System.out.println("🔄 [UPDATE] Iniciando actualización...");
    System.out.println("📥 Request: projectId=" + request.projectId() + 
                      ", title=" + request.title() + 
                      ", description=" + request.description() + 
                      ", categoryId=" + request.categoryId());
    
    Project updatedProject = projectsRepository.findById(request.projectId()).orElse(null);
    if (updatedProject != null) {
        System.out.println("✅ Proyecto encontrado. Datos actuales:");
        System.out.println("   - name: " + updatedProject.getName());
        System.out.println("   - description: " + updatedProject.getDescription());
        System.out.println("   - category: " + (updatedProject.getCategory() != null ? 
                          updatedProject.getCategory().getCategoryId() + " - " + updatedProject.getCategory().getName() : "null"));
        
        // ACTUALIZAR CAMPOS
        System.out.println("🔄 Actualizando name: '" + updatedProject.getName() + "' → '" + request.title() + "'");
        updatedProject.setName(request.title());
        
        System.out.println("🔄 Actualizando description: '" + updatedProject.getDescription() + "' → '" + request.description() + "'");
        updatedProject.setDescription(request.description());
        
        // ACTUALIZAR CATEGORÍA
        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId()).orElse(null);
            System.out.println("🔄 Actualizando categoría: " + 
                (updatedProject.getCategory() != null ? updatedProject.getCategory().getCategoryId() : "null") + 
                " → " + request.categoryId());
            updatedProject.setCategory(category);
        }
        
        // ACTUALIZAR SKILLS (esto ya funciona según los logs)
        System.out.println("🔄 Actualizando skills...");
        updatedProject.setSkills(request.skills());
        
        System.out.println("💾 Guardando proyecto...");
        Project savedProject = projectsRepository.save(updatedProject);
        
        System.out.println("✅ Proyecto guardado. Nuevos datos:");
        System.out.println("   - name: " + savedProject.getName());
        System.out.println("   - description: " + savedProject.getDescription());
        System.out.println("   - category: " + (savedProject.getCategory() != null ? 
                          savedProject.getCategory().getCategoryId() + " - " + savedProject.getCategory().getName() : "null"));
        
        return new CreateProjectResponse(true, "Se actualizó el proyecto");
    }
    
    System.out.println("❌ Proyecto no encontrado");
    return new CreateProjectResponse(false, "Fallo la actualización");
}

    public CreateProjectResponse deleteProject(Integer projectId){
      Project updatedProject = projectsRepository.findById(projectId).orElse(null);
      if (updatedProject != null) {

        projectsRepository.deleteById(projectId);
        return new CreateProjectResponse(true, "Se actualizo el proyecto");
      }
      return new CreateProjectResponse(false, "Fallo la actualizacion");
      
    }

    private List<ProjectResponse> listDtoProjects(List<Project> projects) {
    List<ProjectResponse> projectsResponse = new ArrayList<>();
    
    for (Project project : projects) {
        Set<Task> tasks = taskJdbcRepository.findTasksByProjectId(project.getProjectId());
        
        Set<TaskBasicDTO> taskDTOs = tasks.stream()
            .map(task -> new TaskBasicDTO(
                task.getName() != null ? task.getName() : "Sin nombre",
                task.getState() != null ? task.getState() : "to be done",
                task.getAssignedUser() != null ? String.valueOf(task.getAssignedUser().getUserId()) : "Sin usuario"
            ))
            .collect(Collectors.toSet());
        
        Set<SkillBasicDTO> skillDTOs = new HashSet<>();
        if (project.getSkills() != null) {
            skillDTOs = project.getSkills().stream()
                .map(skill -> new SkillBasicDTO(skill.getName()))
                .collect(Collectors.toSet());
        }
        
        // SOLUCIÓN: Usar new HashSet<>() en lugar de project.getUsers()
        projectsResponse.add(new ProjectResponse(
            project.getProjectId(), 
            project.getName(),
            project.getDescription(), 
            new HashSet<>(),  // ← USERS VACÍO TEMPORALMENTE
            getCategoryName(project.getCategory()),
            skillDTOs,
            taskDTOs
        ));
    }
    return projectsResponse;
}

    private String getCategoryName(Category category) {
        return category != null ? category.getName() : "Sin categoría";
    }

    public CreateProjectResponse createProject(CreateProjectRequest request){
      Project newProject = Project.builder()
                            .name(request.title())
                            .userCreatorId(request.userId())
                            .description(request.description())
                            .category(categoryRepository.findById(request.categoryId()).orElse(null))
                            .skills(request.skills())
                            .build();
      Project projectSaved = projectsRepository.save(newProject);
      return new CreateProjectResponse(true, "Se creo correctamente el proyecto");
    }

}
