package com.partyst.app.partystapp.services;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.partyst.app.partystapp.records.requests.NotificationUserRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.NotificationResponse;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class NotificationService {

    private final WebClient webClient;

    @Value("${microservices.notifications.timeout:10000}")
    private int timeout;

    public NotificationService(
            @Value("${microservices.notifications.url}") String notificationsServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(notificationsServiceUrl)
                .build();
    }

    /**
     * Obtiene todas las notificaciones de un usuario desde el microservicio
     */
    public List<NotificationResponse> getNotificationsByUser(Long userId) {
        try {
            log.info("Obteniendo notificaciones para usuario: {}", userId);
            
            NotificationApiResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/notifications")
                            .queryParam("userid", userId)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        log.error("Error 4xx al obtener notificaciones para usuario {}", userId);
                        return Mono.error(new RuntimeException("Error del cliente al obtener notificaciones"));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        log.error("Error 5xx en el microservicio de notificaciones");
                        return Mono.error(new RuntimeException("Error del servidor de notificaciones"));
                    })
                    .bodyToMono(NotificationApiResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();

            if (response != null && response.success() && response.body() != null) {
                log.info("Notificaciones obtenidas exitosamente: {} notificaciones", 
                        response.body().notifications().size());
                return response.body().notifications();
            }
            
            log.warn("No se obtuvieron notificaciones para el usuario {}", userId);
            return Collections.emptyList();
            
        } catch (Exception e) {
            log.error("Error al comunicarse con el microservicio de notificaciones: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Marca una notificación como leída
     */
    public CreateProjectResponse readNotification(NotificationUserRequest request) {
        try {
            log.info("Marcando notificación {} como leída para usuario {}", 
                    request.notificationId(), request.userId());
            
            Map<String, Object> requestBody = Map.of(
                    "userid", request.userId().toString(),
                    "notificationid", request.notificationId().toString()
            );

            NotificationActionResponse response = webClient.patch()
                    .uri("/notifications/read")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        log.error("Error 4xx al marcar notificación como leída");
                        return Mono.error(new RuntimeException("Notificación no encontrada"));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        log.error("Error 5xx en el microservicio de notificaciones");
                        return Mono.error(new RuntimeException("Error del servidor de notificaciones"));
                    })
                    .bodyToMono(NotificationActionResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();

            if (response != null && response.success()) {
                log.info("Notificación marcada como leída exitosamente");
                return new CreateProjectResponse(true, response.message());
            }
            
            log.warn("No se pudo marcar la notificación como leída");
            return new CreateProjectResponse(false, "No se pudo marcar la notificación como leída");
            
        } catch (Exception e) {
            log.error("Error al marcar notificación como leída: {}", e.getMessage(), e);
            return new CreateProjectResponse(false, "Error: " + e.getMessage());
        }
    }

    /**
     * Elimina una notificación
     */
    public CreateProjectResponse deleteNotification(NotificationUserRequest request) {
        try {
            log.info("Eliminando notificación {} para usuario {}", 
                    request.notificationId(), request.userId());
            
            Map<String, Object> requestBody = Map.of(
                    "userid", request.userId().toString(),
                    "notificationid", request.notificationId().toString()
            );

            NotificationActionResponse response = webClient.method(org.springframework.http.HttpMethod.DELETE)
                    .uri("/notifications")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        log.error("Error 4xx al eliminar notificación");
                        return Mono.error(new RuntimeException("Notificación no encontrada"));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        log.error("Error 5xx en el microservicio de notificaciones");
                        return Mono.error(new RuntimeException("Error del servidor de notificaciones"));
                    })
                    .bodyToMono(NotificationActionResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();

            if (response != null && response.success()) {
                log.info("Notificación eliminada exitosamente");
                return new CreateProjectResponse(true, response.message());
            }
            
            log.warn("No se pudo eliminar la notificación");
            return new CreateProjectResponse(false, "No se pudo eliminar la notificación");
            
        } catch (Exception e) {
            log.error("Error al eliminar notificación: {}", e.getMessage(), e);
            return new CreateProjectResponse(false, "Error: " + e.getMessage());
        }
    }

    /**
     * Crea una nueva notificación (SIN KAFKA)
     * Este método llama directamente al endpoint POST del microservicio
     * 
     * @param userId ID del usuario que recibirá la notificación
     * @param title Título de la notificación
     * @param description Descripción detallada
     * @param type Tipo: warning, success, informative, application
     * @return Respuesta con éxito/error
     */
    public CreateProjectResponse createNotification(
            Long userId, 
            String title, 
            String description, 
            String type) {
        try {
            log.info("Creando notificación para usuario {}: {}", userId, title);
            
            Map<String, Object> requestBody = Map.of(
                    "userid", userId.toString(),
                    "title", title,
                    "description", description,
                    "type", type
            );

            NotificationCreateResponse response = webClient.post()
                    .uri("/notifications")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        log.error("Error 4xx al crear notificación");
                        return Mono.error(new RuntimeException("Error en datos de notificación"));
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                        log.error("Error 5xx en el microservicio de notificaciones");
                        return Mono.error(new RuntimeException("Error del servidor de notificaciones"));
                    })
                    .bodyToMono(NotificationCreateResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .block();

            if (response != null && response.success()) {
                log.info("Notificación creada exitosamente con ID: {}", response.notificationid());
                return new CreateProjectResponse(true, "Notificación creada: " + response.message());
            }
            
            log.warn("No se pudo crear la notificación");
            return new CreateProjectResponse(false, "No se pudo crear la notificación");
            
        } catch (Exception e) {
            log.error("Error al crear notificación: {}", e.getMessage(), e);
            return new CreateProjectResponse(false, "Error: " + e.getMessage());
        }
    }

    // DTOs internos para mapear las respuestas del microservicio
    private record NotificationApiResponse(
            boolean success,
            String message,
            NotificationBody body
    ) {}

    private record NotificationBody(
            List<NotificationResponse> notifications
    ) {}

    private record NotificationActionResponse(
            boolean success,
            String message
    ) {}

    private record NotificationCreateResponse(
            boolean success,
            String message,
            String notificationid
    ) {}
}
