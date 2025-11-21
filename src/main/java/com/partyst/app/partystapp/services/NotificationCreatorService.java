package com.partyst.app.partystapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NotificationCreatorService {

    @Autowired
    private NotificationService notificationService;

    public void notifyNewJoinRequest(Long creatorId, String projectName, String requestingUserName) {
        log.info("Creando notificación de solicitud de membresía para usuario {}", creatorId);

        notificationService.createNotification(
                creatorId,
                "Nueva solicitud de membresía",
                requestingUserName + " quiere unirse al proyecto '" + projectName + "'",
                "application");
    }

    public void notifyJoinRequestAccepted(Long userId, String projectName) {
        log.info("Notificando aceptación de solicitud a usuario {}", userId);

        notificationService.createNotification(
                userId,
                "Solicitud aceptada",
                "Tu solicitud para unirte al proyecto '" + projectName + "' fue aceptada. ¡Bienvenido!",
                "success");
    }

    public void notifyJoinRequestRejected(Long userId, String projectName) {
        log.info("Notificando rechazo de solicitud a usuario {}", userId);

        notificationService.createNotification(
                userId,
                "Solicitud rechazada",
                "Tu solicitud para unirte al proyecto '" + projectName + "' fue rechazada",
                "warning");
    }

    public void notifyTaskAssigned(Long userId, String taskTitle, String projectName) {
        log.info("Notificando asignación de tarea a usuario {}", userId);

        notificationService.createNotification(
                userId,
                "Nueva tarea asignada",
                "Se te asignó la tarea '" + taskTitle + "' en el proyecto '" + projectName + "'",
                "informative");
    }

    public void notifyTaskStatusChanged(Long userId, String taskTitle, String oldStatus, String newStatus) {
        log.info("Notificando cambio de estado de tarea a usuario {}", userId);

        notificationService.createNotification(
                userId,
                "Estado de tarea actualizado",
                "La tarea '" + taskTitle + "' cambió de '" + oldStatus + "' a '" + newStatus + "'",
                "informative");
    }

    public void notifyProjectInvitation(Long userId, String projectName, String invitedBy) {
        log.info("Notificando invitación a proyecto a usuario {}", userId);

        notificationService.createNotification(
                userId,
                "Invitación a proyecto",
                invitedBy + " te invitó al proyecto '" + projectName + "'",
                "application");
    }

    public void notifyMemberRemoved(Long userId, String projectName, String removedBy) {
        log.info("Notificando remoción de proyecto a usuario {}", userId);

        notificationService.createNotification(
                userId,
                "Removido de proyecto",
                "Fuiste removido del proyecto '" + projectName + "' por " + removedBy,
                "warning");
    }

    public void notifyProjectCompleted(Long userId, String projectName) {
        log.info("Notificando proyecto completado a usuario {}", userId);

        notificationService.createNotification(
                userId,
                "Proyecto completado",
                "El proyecto '" + projectName + "' ha sido marcado como completado. ¡Felicitaciones!",
                "success");
    }

    public void notifyTaskComment(Long userId, String taskTitle, String commenterName) {
        log.info("Notificando comentario en tarea a usuario {}", userId);

        notificationService.createNotification(
                userId,
                "Nuevo comentario en tarea",
                commenterName + " comentó en la tarea '" + taskTitle + "'",
                "informative");
    }

    public void notifyCustom(Long userId, String type, String title, String message) {
        log.info("Creando notificación personalizada para usuario {}", userId);

        notificationService.createNotification(userId, title, message, type);
    }

    public void notifyPendingTasks(Long userId, int taskCount) {
        if (taskCount > 0) {
            log.info("Notificando tareas pendientes a usuario {}", userId);

            String message = taskCount == 1
                    ? "Tienes 1 tarea pendiente por completar"
                    : "Tienes " + taskCount + " tareas pendientes por completar";

            notificationService.createNotification(
                    userId,
                    "Tareas pendientes",
                    message,
                    "informative");
        }
    }

    public void notifyApproachingDeadline(Long userId, String projectName, int daysLeft) {
        log.info("Notificando deadline próximo a usuario {}", userId);

        String urgency = daysLeft <= 1 ? "¡URGENTE! " : "";
        String days = daysLeft == 1 ? "1 día" : daysLeft + " días";

        notificationService.createNotification(
                userId,
                urgency + "Deadline próximo",
                "El proyecto '" + projectName + "' vence en " + days,
                "warning");
    }
}
