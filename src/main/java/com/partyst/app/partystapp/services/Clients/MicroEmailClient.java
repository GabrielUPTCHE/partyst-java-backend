package com.partyst.app.partystapp.services.Clients;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MicroEmailClient {

   private final RestTemplate restTemplate = new RestTemplate();
   
   public Object sendEmail(String email, int code) {
        String url = "https://email-service-hkvt.onrender.com/api/v1/emails/send";
        Map<String, Object> body = new HashMap<>();
        body.put("to_email", email);
        body.put("subject", "Restablecer contraseña.");
        body.put("body", "<h1>Codigo para restablecer contraseña <b>"+ code +"</b></h1>");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
    return response.getBody();
}

}
