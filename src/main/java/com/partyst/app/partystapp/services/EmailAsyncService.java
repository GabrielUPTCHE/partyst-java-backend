package com.partyst.app.partystapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.services.Clients.MicroEmailClient;

@Service
public class EmailAsyncService {

    @Autowired
    private MicroEmailClient microEmailClient;

    @Async("taskExecutor")
    public void sendEmailAsync(String email, int code) {
        try {
            Object result = microEmailClient.sendEmail(email, code);
        } catch (Exception ex) {
            System.out.println("ERROR al enviar email : " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}