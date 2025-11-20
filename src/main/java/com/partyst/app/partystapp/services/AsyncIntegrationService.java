package com.partyst.app.partystapp.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.entities.RecoveryCode;
import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.responses.ForgetPasswordResponse;
import com.partyst.app.partystapp.repositories.RecoveryCodeRepository;
import com.partyst.app.partystapp.repositories.UserRepository;

@Service
public class AsyncIntegrationService {

    @Autowired
    private EmailAsyncService emailAsyncService;

    @Autowired
    private RecoveryCodeRepository recoveryCodeRepository;

    @Autowired
    private UserRepository userRepository;

    private Random random = new Random();

    public ForgetPasswordResponse sendEmail(String email) {
        System.out.println("hizo esto");
        int code = random.nextInt(900000) + 100000;
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return new ForgetPasswordResponse("El correo no esta registrado", false);
        }
        
        System.out.println("Guardando código de recuperación...");
        recoveryCodeRepository.save(
                RecoveryCode.builder()
                        .email(email)
                        .tokenRecovery(Integer.toString(code))
                        .build());
        System.out.println("Código guardado ✔");
        
        System.out.println("Llamando al método asíncrono...");
        emailAsyncService.sendEmailAsync(email, code);
        System.out.println("Método asíncrono invocado");

        return new ForgetPasswordResponse("Se envio correo de confirmacion", true);
    }
}