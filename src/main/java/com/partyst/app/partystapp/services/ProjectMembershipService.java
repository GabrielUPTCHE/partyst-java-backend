package com.partyst.app.partystapp.services;

import com.partyst.app.partystapp.entities.Project;
import com.partyst.app.partystapp.entities.ProjectRequest;
import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.requests.AcceptRequestRequest;
import com.partyst.app.partystapp.records.requests.JoinProjectRequest;
import com.partyst.app.partystapp.records.requests.RejectRequestRequest;
import com.partyst.app.partystapp.records.requests.RemoveMemberRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.JoinProjectResponse;
import com.partyst.app.partystapp.records.responses.ProjectMembersResponse;
import com.partyst.app.partystapp.records.responses.ProjectRequestsResponse;
import com.partyst.app.partystapp.repositories.ProjectsRepository;
import com.partyst.app.partystapp.repositories.ProjectRequestRepository;
import com.partyst.app.partystapp.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProjectMembershipService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectMembershipService.class);

    @Autowired
    private ProjectsRepository projectsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRequestRepository projectRequestRepository;

    /**
     * Solicitar unirse a un proyecto
     */
    public JoinProjectResponse joinProject(JoinProjectRequest request) {
        try {
            logger.info("🔔 [JOIN REQUEST] Usuario {} solicitando unirse al proyecto {}", 
                request.userId(), request.projectId());

            // Validar que el proyecto existe
            Project project = projectsRepository.findById(request.projectId())
                .orElseThrow(() -> {
                    logger.error("❌ Proyecto no encontrado: {}", request.projectId());
                    return new IllegalArgumentException("Proyecto no encontrado");
                });

            // Validar que el usuario existe
            User user = userRepository.findById(request.userId().longValue())
                .orElseThrow(() -> {
                    logger.error("❌ Usuario no encontrado: {}", request.userId());
                    return new IllegalArgumentException("Usuario no encontrado");
                });

            // Verificar si el usuario ya es miembro
            if (project.getUsers() != null && project.getUsers().contains(user)) {
                logger.warn("⚠️ Usuario {} ya es miembro del proyecto {}", user.getEmail(), project.getName());
                return new JoinProjectResponse(301, "Ya eres miembro de este proyecto");
            }

            // Verificar si ya existe una solicitud pendiente
            boolean existsPendingRequest = projectRequestRepository
                .existsByProjectAndUserAndStatus(project, user, "pending");

            if (existsPendingRequest) {
                logger.warn("⚠️ Ya existe una solicitud pendiente para usuario {} en proyecto {}", 
                    user.getEmail(), project.getName());
                return new JoinProjectResponse(301, "Ya tienes una solicitud pendiente para este proyecto");
            }

            // Crear la solicitud
            ProjectRequest projectRequest = ProjectRequest.builder()
                .project(project)
                .user(user)
                .message(request.message() != null ? request.message() : "Solicitud para unirse al proyecto")
                .status("pending")
                .createdAt(LocalDateTime.now())
                .build();

            projectRequestRepository.save(projectRequest);
            
            logger.info("✅ Solicitud creada exitosamente para usuario {} en proyecto {}", 
                user.getEmail(), project.getName());

            return new JoinProjectResponse(201, "Solicitud enviada exitosamente");

        } catch (IllegalArgumentException e) {
            logger.error("❌ Error de validación: {}", e.getMessage());
            return new JoinProjectResponse(301, e.getMessage());
        } catch (Exception e) {
            logger.error("❌ Error inesperado al unirse al proyecto: {}", e.getMessage(), e);
            return new JoinProjectResponse(500, "Error interno del servidor");
        }
    }

    /**
     * Obtener solicitudes pendientes de un proyecto
     */
    public ProjectRequestsResponse getProjectRequests(Integer projectId) {
        logger.info("📋 [GET REQUESTS] Obteniendo solicitudes del proyecto {}", projectId);

        Project project = projectsRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + projectId));

        List<ProjectRequest> requests = projectRequestRepository.findByProjectAndStatus(project, "pending");

        List<ProjectRequestsResponse.RequestInfo> requestInfos = requests.stream()
            .map(req -> new ProjectRequestsResponse.RequestInfo(
                req.getUser().getUserId().intValue(),
                req.getUser().getName(),
                req.getUser().getLastname(),
                req.getUser().getNickname(),
                req.getUser().getEmail(),
                req.getMessage(),
                req.getRequestDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ))
            .collect(Collectors.toList());

        logger.info("✅ Encontradas {} solicitudes pendientes", requestInfos.size());

        return new ProjectRequestsResponse(
            true,
            "Solicitudes obtenidas exitosamente",
            new ProjectRequestsResponse.Data(requestInfos)
        );
    }

    /**
     * Aceptar solicitud de un usuario
     */
    @Transactional
    public CreateProjectResponse acceptRequest(AcceptRequestRequest request) {
        logger.info("✅ [ACCEPT REQUEST] Aceptando solicitud - Proyecto: {}, Usuario: {}", 
            request.projectid(), request.userid());

        Project project = projectsRepository.findById(request.projectid())
            .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + request.projectid()));

        User user = userRepository.findById(Long.valueOf(request.userid()))
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + request.userid()));

        ProjectRequest projectRequest = projectRequestRepository
            .findByProjectAndUserAndStatus(project, user, "pending")
            .orElseThrow(() -> new IllegalArgumentException("No se encontró solicitud pendiente"));

        // Actualizar estado de la solicitud
        projectRequest.setStatus("accepted");
        projectRequestRepository.save(projectRequest);

        // Agregar usuario al proyecto
        if (project.getUsers() == null) {
            project.setUsers(new HashSet<>());
        }
        project.getUsers().add(user);
        projectsRepository.save(project);

        logger.info("✅ Usuario {} agregado exitosamente al proyecto {}", 
            user.getEmail(), project.getName());

        return new CreateProjectResponse(true, "Solicitud aceptada y usuario agregado al proyecto");
    }

    /**
     * Rechazar solicitud de un usuario
     */
    @Transactional
    public CreateProjectResponse rejectRequest(RejectRequestRequest request) {
        logger.info("❌ [REJECT REQUEST] Rechazando solicitud - Proyecto: {}, Usuario: {}", 
            request.projectid(), request.userid());

        Project project = projectsRepository.findById(request.projectid())
            .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + request.projectid()));

        User user = userRepository.findById(Long.valueOf(request.userid()))
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + request.userid()));

        ProjectRequest projectRequest = projectRequestRepository
            .findByProjectAndUserAndStatus(project, user, "pending")
            .orElseThrow(() -> new IllegalArgumentException("No se encontró solicitud pendiente"));

        // Actualizar estado de la solicitud
        projectRequest.setStatus("rejected");
        projectRequestRepository.save(projectRequest);

        logger.info("✅ Solicitud rechazada para usuario {} en proyecto {}", 
            user.getEmail(), project.getName());

        return new CreateProjectResponse(true, "Solicitud rechazada");
    }

    /**
     * Obtener miembros de un proyecto
     */
    public ProjectMembersResponse getProjectMembers(Integer projectId) {
        logger.info("👥 [GET MEMBERS] Obteniendo miembros del proyecto {}", projectId);

        Project project = projectsRepository.findById(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + projectId));

        Set<User> members = project.getUsers() != null ? project.getUsers() : new HashSet<>();

        List<ProjectMembersResponse.MemberInfo> memberInfos = members.stream()
            .map(user -> new ProjectMembersResponse.MemberInfo(
                user.getUserId().intValue(),
                user.getName(),
                user.getLastname(),
                user.getNickname(),
                user.getEmail(),
                user.getUserId().intValue() == project.getUserCreatorId()
            ))
            .collect(Collectors.toList());

        logger.info("✅ Encontrados {} miembros", memberInfos.size());

        return new ProjectMembersResponse(
            true,
            "Miembros obtenidos exitosamente",
            new ProjectMembersResponse.Data(memberInfos)
        );
    }

    /**
     * Eliminar miembro de un proyecto
     */
    @Transactional
    public CreateProjectResponse removeMember(RemoveMemberRequest request) {
        logger.info("🗑️ [REMOVE MEMBER] Eliminando miembro - Proyecto: {}, Usuario: {}", 
            request.projectid(), request.userid());

        Project project = projectsRepository.findById(request.projectid())
            .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + request.projectid()));

        User user = userRepository.findById(Long.valueOf(request.userid()))
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + request.userid()));

        // Verificar que no sea el creador
        if (user.getUserId().intValue() == project.getUserCreatorId()) {
            logger.warn("⚠️ No se puede eliminar al creador del proyecto");
            return new CreateProjectResponse(false, "No se puede eliminar al creador del proyecto");
        }

        // Remover usuario del proyecto
        if (project.getUsers() != null && project.getUsers().remove(user)) {
            projectsRepository.save(project);
            logger.info("✅ Usuario {} eliminado del proyecto {}", user.getEmail(), project.getName());
            return new CreateProjectResponse(true, "Miembro eliminado exitosamente");
        }

        logger.warn("⚠️ Usuario {} no es miembro del proyecto {}", user.getEmail(), project.getName());
        return new CreateProjectResponse(false, "El usuario no es miembro del proyecto");
    }
}
