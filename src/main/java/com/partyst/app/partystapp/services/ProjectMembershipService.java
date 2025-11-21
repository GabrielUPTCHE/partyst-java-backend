package com.partyst.app.partystapp.services;

import com.partyst.app.partystapp.entities.Project;
import com.partyst.app.partystapp.entities.ProjectRequest;
import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.requests.AcceptRequestRequest;
import com.partyst.app.partystapp.records.requests.JoinProjectRequest;
import com.partyst.app.partystapp.records.requests.RejectRequestRequest;
import com.partyst.app.partystapp.records.requests.RemoveMemberRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    @Transactional
    public CreateProjectResponse joinProject(JoinProjectRequest request) {
        logger.info(" [JOIN REQUEST] Usuario {} solicitando unirse al proyecto {}",
                request.userId(), request.projectId());

        Project project = projectsRepository.findById(request.projectId())
                .orElseThrow(
                        () -> new IllegalArgumentException("Proyecto no encontrado con ID: " + request.projectId()));

        User user = userRepository.findById(Long.valueOf(request.userId()))
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + request.userId()));

        if (project.getUsers() != null && project.getUsers().contains(user)) {
            logger.warn(" Usuario {} ya es miembro del proyecto {}", user.getEmail(), project.getName());
            return new CreateProjectResponse(false, "Ya eres miembro de este proyecto");
        }

        boolean existsPendingRequest = projectRequestRepository
                .existsByProjectAndUserAndStatus(project, user, "pending");

        if (existsPendingRequest) {
            logger.warn(" Ya existe una solicitud pendiente para usuario {} en proyecto {}",
                    user.getEmail(), project.getName());
            return new CreateProjectResponse(false, "Ya tienes una solicitud pendiente para este proyecto");
        }

        ProjectRequest projectRequest = ProjectRequest.builder()
                .project(project)
                .user(user)
                .message(request.message())
                .status("pending")
                .build();

        projectRequestRepository.save(projectRequest);

        logger.info(" Solicitud creada exitosamente para usuario {} en proyecto {}",
                user.getEmail(), project.getName());

        return new CreateProjectResponse(true, "Solicitud enviada exitosamente");
    }

    public ProjectRequestsResponse.Data getProjectRequests(Integer projectId) {
        logger.info(" [GET REQUESTS] Obteniendo solicitudes del proyecto {}", projectId);

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
                        req.getRequestDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .collect(Collectors.toList());

        logger.info(" Encontradas {} solicitudes pendientes", requestInfos.size());

        return new ProjectRequestsResponse.Data(requestInfos);
    }

    @Transactional
    public CreateProjectResponse acceptRequest(AcceptRequestRequest request) {
        logger.info(" [ACCEPT REQUEST] Aceptando solicitud - Proyecto: {}, Usuario: {}",
                request.projectid(), request.userid());

        Project project = projectsRepository.findById(request.projectid())
                .orElseThrow(
                        () -> new IllegalArgumentException("Proyecto no encontrado con ID: " + request.projectid()));

        User user = userRepository.findById(Long.valueOf(request.userid()))
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + request.userid()));

        ProjectRequest projectRequest = projectRequestRepository
                .findByProjectAndUserAndStatus(project, user, "pending")
                .orElseThrow(() -> new IllegalArgumentException("No se encontró solicitud pendiente"));

        projectRequest.setStatus("accepted");
        projectRequestRepository.save(projectRequest);

        if (project.getUsers() == null) {
            project.setUsers(new HashSet<>());
        }
        project.getUsers().add(user);
        projectsRepository.save(project);

        logger.info(" Usuario {} agregado exitosamente al proyecto {}",
                user.getEmail(), project.getName());

        return new CreateProjectResponse(true, "Solicitud aceptada y usuario agregado al proyecto");
    }

    @Transactional
    public CreateProjectResponse rejectRequest(RejectRequestRequest request) {
        logger.info(" [REJECT REQUEST] Rechazando solicitud - Proyecto: {}, Usuario: {}",
                request.projectid(), request.userid());

        Project project = projectsRepository.findById(request.projectid())
                .orElseThrow(
                        () -> new IllegalArgumentException("Proyecto no encontrado con ID: " + request.projectid()));

        User user = userRepository.findById(Long.valueOf(request.userid()))
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + request.userid()));

        ProjectRequest projectRequest = projectRequestRepository
                .findByProjectAndUserAndStatus(project, user, "pending")
                .orElseThrow(() -> new IllegalArgumentException("No se encontró solicitud pendiente"));

        projectRequest.setStatus("rejected");
        projectRequestRepository.save(projectRequest);

        return new CreateProjectResponse(true, "Solicitud rechazada");
    }

    public ProjectMembersResponse getProjectMembers(Integer projectId) {
        Project project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado con ID: " + projectId));

        User creator = userRepository.findById(project.getUserCreatorId().longValue())
                .orElseThrow(() -> new IllegalArgumentException("Usuario creador no encontrado"));

        Set<User> members = project.getUsers() != null ? project.getUsers() : new HashSet<>();

        List<User> allMembers = new ArrayList<>();
        allMembers.add(creator);
        allMembers.addAll(members.stream()
                .filter(member -> !member.getUserId().equals(creator.getUserId()))
                .collect(Collectors.toList()));

        List<ProjectMembersResponse.MemberInfo> memberInfos = allMembers.stream()
                .map(user -> new ProjectMembersResponse.MemberInfo(
                        user.getUserId().intValue(),
                        user.getName(),
                        user.getLastname(),
                        user.getNickname(),
                        user.getEmail(),
                        user.getUserId().equals(creator.getUserId())))
                .collect(Collectors.toList());

        return new ProjectMembersResponse(memberInfos);
    }

    @Transactional
    public CreateProjectResponse removeMember(RemoveMemberRequest request) {
        logger.info(" [REMOVE MEMBER] Eliminando miembro - Proyecto: {}, Usuario: {}",
                request.projectid(), request.userid());

        Project project = projectsRepository.findById(request.projectid())
                .orElseThrow(
                        () -> new IllegalArgumentException("Proyecto no encontrado con ID: " + request.projectid()));

        User user = userRepository.findById(Long.valueOf(request.userid()))
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + request.userid()));

        if (user.getUserId().intValue() == project.getUserCreatorId()) {
            logger.warn(" No se puede eliminar al creador del proyecto");
            return new CreateProjectResponse(false, "No se puede eliminar al creador del proyecto");
        }

        if (project.getUsers() != null && project.getUsers().remove(user)) {
            projectsRepository.save(project);
            logger.info(" Usuario {} eliminado del proyecto {}", user.getEmail(), project.getName());
            return new CreateProjectResponse(true, "Miembro eliminado exitosamente");
        }

        return new CreateProjectResponse(false, "El usuario no es miembro del proyecto");
    }
}
