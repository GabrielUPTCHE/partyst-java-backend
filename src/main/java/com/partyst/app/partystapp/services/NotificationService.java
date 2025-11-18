package com.partyst.app.partystapp.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.records.requests.NotificationUserRequest;
import com.partyst.app.partystapp.records.responses.CreateProjectResponse;
import com.partyst.app.partystapp.records.responses.NotificationResponse;

@Service
public class NotificationService {


    public List<NotificationResponse> getNotificationsByUser(Long userId){
        //implementar comunicacion 
        return List.of(new NotificationResponse(1,"prueba","descripcion","warning", "12-2-2025",false));
    }
    
    public CreateProjectResponse readNotification(NotificationUserRequest request){
        //implementar comunicacion 
        return new CreateProjectResponse(true, "Notificacion leida");
    }

    public CreateProjectResponse deleteNotification(NotificationUserRequest request){
        //implementar comunicacion 
        return new CreateProjectResponse(true, "Notificacion eliminada");
    }
}
