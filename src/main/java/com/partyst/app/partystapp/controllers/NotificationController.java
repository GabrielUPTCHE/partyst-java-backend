package com.partyst.app.partystapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partyst.app.partystapp.entities.Task;
import com.partyst.app.partystapp.records.GenericResponse;
import com.partyst.app.partystapp.records.requests.NotificationUserRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.NotificationResponse;
import com.partyst.app.partystapp.services.NotificationService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/notifications")
public class NotificationController {


    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<GenericResponse>  getNotificationsByUser(@PathVariable Long userId) {
        List<NotificationResponse> notificationsFinded = notificationService.getNotificationsByUser( userId);
        return ResponseEntity.ok(new GenericResponse<List<NotificationResponse>>(201, "Notificaciones encontradas", notificationsFinded));
    }
    
    @PatchMapping("/read")
    public ResponseEntity<GenericResponse>  updateReadNotification(@RequestBody NotificationUserRequest request) {
        CreateProjectResponse notificationRead = notificationService.readNotification( request);
        return ResponseEntity.ok(new GenericResponse<CreateProjectResponse>(201, "Notificacion leida", notificationRead));
    }
    @DeleteMapping("/delete")
    public ResponseEntity<GenericResponse>  deleteNotification(@RequestBody NotificationUserRequest request) {
        CreateProjectResponse notificationRead = notificationService.deleteNotification( request);
        return ResponseEntity.ok(new GenericResponse<CreateProjectResponse>(201, "Notificacion eliminada", notificationRead));
    }
    
    
}
