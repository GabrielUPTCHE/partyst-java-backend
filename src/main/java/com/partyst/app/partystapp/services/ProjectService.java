package com.partyst.app.partystapp.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.partyst.app.partystapp.config.ProjectSpecifications;
import com.partyst.app.partystapp.entities.Category;
import com.partyst.app.partystapp.entities.Project;
import com.partyst.app.partystapp.entities.Skill;
import com.partyst.app.partystapp.entities.Task;
import com.partyst.app.partystapp.entities.User;
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
import com.partyst.app.partystapp.repositories.SkillRepository;
import com.partyst.app.partystapp.repositories.TaskJdbcRepository;
import com.partyst.app.partystapp.repositories.TaskRepository;
import com.partyst.app.partystapp.repositories.UserRepository;

@Service
public class ProjectService {

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TaskJdbcRepository taskJdbcRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SkillRepository skillRepository;

    public List<ProjectResponse> getProjectsByIdUser(Integer userId) {
        List<Project> findedProjects = projectsRepository.findAllByUsersUserId(userId);
        return listDtoProjects(findedProjects);
    }

    public List<Project> getAll() {
        List<Project> findedProjects = projectsRepository.findAllWithFullData();
        return findedProjects;
    }

    public List<ProjectBasicResponse> filterProjects(FilterProjectRequest filters) {

        Specification<Project> spec = Specification.where(null);

        spec = spec.and(ProjectSpecifications.hasTitle(filters.title()));

        spec = spec.and(ProjectSpecifications.hasCategory(filters.category()));

        spec = spec.and(ProjectSpecifications.hasSkills(filters.skills()));

        spec = spec.and(ProjectSpecifications.isActive(filters.active()));

        if ("public".equalsIgnoreCase(filters.type())) {
            spec = spec.and(ProjectSpecifications.isPublic());

        } else if ("registered".equalsIgnoreCase(filters.type())) {
            spec = spec.and(ProjectSpecifications.hasUserRegistered(filters.userId()));

        } else if ("profile".equalsIgnoreCase(filters.type())) {
            spec = spec.and(ProjectSpecifications.hasUserCreator(filters.userId()));
        }

        List<Project> projects = projectsRepository.findAll(spec);

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
                                : List.of()))
                .toList();
    }

    public ProjectResponse getProjectById(Integer projectId) {
        try {
            List<Project> projects = projectsRepository.findByProjectId(projectId);
            if (projects.isEmpty()) {
                System.out.println(" [3] No hay proyectos");
                return null; 
            }

            Project project = projects.get(0);
            System.out.println("✅ [4] Proyecto encontrado: " + project.getName());

            ProjectResponse response = convertToProjectResponse(project);

            return response;

        } catch (Exception e) {
            System.err.println("[ERROR] En getProjectById: " + e.getMessage());
            e.printStackTrace();
            return null; 
        }
    }

    private ProjectResponse convertToProjectResponse(Project project) {
        Set<Task> tasks = taskJdbcRepository.findTasksByProjectId(project.getProjectId());

        Set<TaskBasicDTO> taskDTOs = tasks.stream()
                .map(task -> {
                    TaskBasicDTO dto = new TaskBasicDTO(
                            task.getTaskId(),
                            task.getName() != null ? task.getName() : "Sin nombre",
                            task.getState() != null ? task.getState() : "to be done",
                            task.getAssignedUser() != null ? task.getAssignedUser().getEmail() : "Sin email",
                            task.getAssignedUser() != null ? task.getAssignedUser().getName() : "Sin nombre",
                            task.getAssignedUser() != null ? task.getAssignedUser().getLastname() : "Sin apellido");
                    return dto;
                })
                .collect(Collectors.toSet());
        Set<SkillBasicDTO> skillDTOs = new HashSet<>();
        if (project.getSkills() != null) {
            skillDTOs = project.getSkills().stream()
                    .map(skill -> new SkillBasicDTO(
                            skill.getName() != null ? skill.getName() : "Sin nombre"))
                    .collect(Collectors.toSet());
        }
        return new ProjectResponse(
                project.getProjectId(),
                project.getName(),
                project.getDescription(),
                project.getUsers(),
                getCategoryName(project.getCategory()),
                skillDTOs,
                taskDTOs);
    }

    @Transactional
    public CreateProjectResponse updateProject(UpdateProjectRequest request) {
        Project updatedProject = projectsRepository.findById(request.projectId()).orElse(null);
        if (updatedProject != null) {
            if (request.categoryId() != null) {
                Category category = categoryRepository.findById(request.categoryId()).orElse(null);

                updatedProject.setCategory(category);
            } else {
                System.out.println("No se proporcionó categoryId, manteniendo categoría actual");
            }

            if (request.skills() != null && !request.skills().isEmpty()) {

                Set<Skill> managedSkills = new HashSet<>();

                for (Skill requestSkill : request.skills()) {

                    if (requestSkill.getSkillId() != null) {
                        Skill existingSkill = skillRepository.findById(requestSkill.getSkillId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Skill no encontrado con ID: " + requestSkill.getSkillId()));
                        managedSkills.add(existingSkill);
                    } else {
                        if (requestSkill.getName() == null || requestSkill.getName().trim().isEmpty()) {
                            continue;
                        }
                        Skill newSkill = new Skill();
                        newSkill.setName(requestSkill.getName().trim());
                        Skill savedSkill = skillRepository.save(newSkill);
                        managedSkills.add(savedSkill);
                        System.out.println("     🆕 Nuevo skill creado: " + savedSkill.getName());
                    }
                }

                if (updatedProject.getSkills() != null) {
                    updatedProject.getSkills().clear();
                }
                updatedProject.setSkills(managedSkills);
                System.out.println("   - Total skills asignados: " + managedSkills.size());
            } else {
                if (updatedProject.getSkills() != null) {
                    updatedProject.getSkills().clear();
                }
                System.out.println("   - Skills limpiados (request sin skills)");
            }

            // 4. GUARDAR PROYECTO
            System.out.println("💾 Guardando proyecto...");
            Project savedProject = projectsRepository.save(updatedProject);

            // Forzar flush para ver errores inmediatamente
            projectsRepository.flush();

            System.out.println("✅ Proyecto guardado exitosamente");
            System.out.println("📊 Resumen final:");
            System.out.println("   - Nombre: " + savedProject.getName());
            System.out.println("   - Descripción: " + savedProject.getDescription());
            System.out.println("   - Categoría: " + (savedProject.getCategory() != null
                    ? savedProject.getCategory().getCategoryId() + " - " + savedProject.getCategory().getName()
                    : "null"));
            System.out.println(
                    "   - Skills: " + (savedProject.getSkills() != null ? savedProject.getSkills().size() : 0));
            if (savedProject.getSkills() != null) {
                savedProject.getSkills().forEach(
                        skill -> System.out.println("     • " + skill.getName() + " (ID: " + skill.getSkillId() + ")"));
            }

            return new CreateProjectResponse(true, "Se actualizó el proyecto");
        }

        System.out.println("❌ Proyecto no encontrado");
        return new CreateProjectResponse(false, "Fallo la actualización");
    }

    @Transactional
    public CreateProjectResponse deleteProject(Integer projectId) {
        System.out.println("🗑️ [PROJECT DELETE] Eliminando proyecto...");
        System.out.println("📥 Request: projectId=" + projectId);

        Project project = projectsRepository.findById(projectId).orElse(null);
        if (project != null) {
            System.out.println("✅ Proyecto encontrado: " + project.getName());

            // 1. Eliminar todas las tareas asociadas al proyecto
            List<Task> tasks = taskRepository.findByProjectId(projectId);
            System.out.println("📋 Tareas encontradas para eliminar: " + tasks.size());

            if (!tasks.isEmpty()) {
                taskRepository.deleteByProjectId(projectId);
                System.out.println("✅ " + tasks.size() + " tarea(s) eliminada(s)");
            }

            // 2. Limpiar las relaciones ManyToMany (colaboraciones y skills)
            try {
                if (project.getUsers() != null && !project.getUsers().isEmpty()) {
                    project.getUsers().clear();
                    System.out.println("✅ Colaboraciones limpiadas");
                }
            } catch (Exception e) {
                System.out.println("⚠️ No hay colaboraciones para limpiar o error: " + e.getMessage());
            }

            try {
                if (project.getSkills() != null && !project.getSkills().isEmpty()) {
                    project.getSkills().clear();
                    System.out.println("✅ Skills limpiados");
                }
            } catch (Exception e) {
                System.out.println("⚠️ No hay skills para limpiar o error: " + e.getMessage());
            }

            // Guardar para que se eliminen las relaciones
            projectsRepository.save(project);

            // 3. Finalmente eliminar el proyecto
            projectsRepository.deleteById(projectId);
            System.out.println("✅ Proyecto eliminado exitosamente");
            return new CreateProjectResponse(true, "Se eliminó el proyecto");
        }

        System.err.println("❌ Proyecto no encontrado con ID: " + projectId);
        return new CreateProjectResponse(false, "Error al eliminar el proyecto");
    }

    private List<ProjectResponse> listDtoProjects(List<Project> projects) {
        List<ProjectResponse> projectsResponse = new ArrayList<>();

        for (Project project : projects) {
            Set<Task> tasks = taskJdbcRepository.findTasksByProjectId(project.getProjectId());

            Set<TaskBasicDTO> taskDTOs = tasks.stream()
                    .map(task -> new TaskBasicDTO(
                            task.getTaskId(),
                            task.getName() != null ? task.getName() : "Sin nombre",
                            task.getState() != null ? task.getState() : "to be done",
                            task.getAssignedUser() != null ? task.getAssignedUser().getEmail() : "Sin email",
                            task.getAssignedUser() != null ? task.getAssignedUser().getName() : "Sin nombre",
                            task.getAssignedUser() != null ? task.getAssignedUser().getLastname() : "Sin apellido"))
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
                    new HashSet<>(), // ← USERS VACÍO TEMPORALMENTE
                    getCategoryName(project.getCategory()),
                    skillDTOs,
                    taskDTOs));
        }
        return projectsResponse;
    }

    private String getCategoryName(Category category) {
        return category != null ? category.getName() : "Sin categoría";
    }

    public CreateProjectResponse createProject(CreateProjectRequest request) {
        try {
            System.out.println("🆕 [CREATE] Creando nuevo proyecto...");
            System.out.println("📥 Request: title=" + request.title() +
                    ", userId=" + request.userId() +
                    ", categoryId=" + request.categoryId() +
                    ", tasks=" + (request.tasks() != null ? request.tasks().size() : 0) +
                    ", skills=" + (request.skills() != null ? request.skills().size() : 0));

            // Validar datos básicos
            if (request.title() == null || request.title().trim().isEmpty()) {
                System.err.println("❌ Título del proyecto es requerido");
                return new CreateProjectResponse(false, "El título del proyecto es requerido");
            }

            if (request.userId() == null) {
                System.err.println("❌ Usuario creador es requerido");
                return new CreateProjectResponse(false, "El usuario creador es requerido");
            }

            // Convertir los SkillIdDTO a Set<Skill>
            Set<com.partyst.app.partystapp.entities.Skill> skillsSet = new HashSet<>();
            if (request.skills() != null && !request.skills().isEmpty()) {
                System.out.println("🔍 Buscando skills por IDs...");
                for (var skillDTO : request.skills()) {
                    var skill = skillRepository.findById(skillDTO.skillId()).orElse(null);
                    if (skill != null) {
                        skillsSet.add(skill);
                        System.out.println("  ✅ Skill encontrado: " + skill.getName());
                    } else {
                        System.err.println("  ⚠️ Skill no encontrado con ID: " + skillDTO.skillId());
                    }
                }
            }

            Project newProject = Project.builder()
                    .name(request.title())
                    .userCreatorId(request.userId())
                    .description(request.description())
                    .category(categoryRepository.findById(request.categoryId()).orElse(null))
                    .skills(skillsSet)
                    .build();

            System.out.println("💾 Guardando proyecto...");
            Project projectSaved = projectsRepository.save(newProject);
            System.out.println("✅ Proyecto guardado con ID: " + projectSaved.getProjectId());

            // Guardar las tareas iniciales si existen
            if (request.tasks() != null && !request.tasks().isEmpty()) {
                System.out.println("📝 Guardando " + request.tasks().size() + " tareas iniciales...");

                // Obtener el usuario creador para asignarlo a las tareas
                User creator = userRepository.findById(request.userId().longValue()).orElse(null);

                if (creator == null) {
                    System.err.println("⚠️ Usuario creador no encontrado con ID: " + request.userId());
                }

                for (var taskDTO : request.tasks()) {
                    // Crear nueva tarea desde el DTO
                    Task task = Task.builder()
                            .name(taskDTO.name())
                            .state(taskDTO.state() != null ? taskDTO.state() : "to be done")
                            .projectId(projectSaved.getProjectId())
                            .assignedUser(creator)
                            .build();

                    System.out.println("  - Guardando tarea: " + task.getName() +
                            " (estado: " + task.getState() +
                            ", asignado a: " + (creator != null ? creator.getName() : "null") + ")");
                    taskRepository.save(task);
                }

                System.out.println("✅ Tareas guardadas correctamente");
            } else {
                System.out.println("⚠️ No hay tareas iniciales para guardar");
            }

            return new CreateProjectResponse(true, "Se creó correctamente el proyecto");

        } catch (Exception e) {
            System.err.println("❌ Error al crear proyecto: " + e.getMessage());
            e.printStackTrace();
            return new CreateProjectResponse(false, "Error al crear el proyecto: " + e.getMessage());
        }
    }
}
