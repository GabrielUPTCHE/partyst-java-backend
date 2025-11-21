package com.partyst.app.partystapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.partyst.app.partystapp.auth.service.AuthService;
import com.partyst.app.partystapp.records.GenericResponse;
import com.partyst.app.partystapp.records.requests.ForgetPasswordRequest;
import com.partyst.app.partystapp.records.requests.LoginRequest;
import com.partyst.app.partystapp.records.requests.RegisterRequest;
import com.partyst.app.partystapp.records.requests.ResetPasswordRequest;
import com.partyst.app.partystapp.records.requests.ValidateCodePasswordRequest;
import com.partyst.app.partystapp.records.responses.ForgetPasswordResponse;
import com.partyst.app.partystapp.records.responses.TokenResponse;
import com.partyst.app.partystapp.services.AsyncIntegrationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpHeaders;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AsyncIntegrationService asyncIntegrationService;


    @PostMapping("/register")
    public ResponseEntity<GenericResponse> register(@RequestBody RegisterRequest request) {   
        TokenResponse tokenResponse = authService.register(request);
        return ResponseEntity.ok(new GenericResponse<TokenResponse>(200,"Se creo el usuario",tokenResponse));
    }
    

    @PostMapping("/login")
    public ResponseEntity<GenericResponse> authenticate(@RequestBody LoginRequest request) {
        TokenResponse entity = authService.login(request);        
        return ResponseEntity.ok(new GenericResponse<TokenResponse>(200, "Login exitoso", entity));
    }

    @PostMapping("/refresh")
    public ResponseEntity<GenericResponse> refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        TokenResponse refreshResponse = authService.refreshToken(authHeader);
        return  ResponseEntity.ok(new GenericResponse<TokenResponse>(200, "Refresco exitoso", refreshResponse));
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<GenericResponse> forgetPassword(@RequestBody ForgetPasswordRequest request){
        ForgetPasswordResponse forgetPasswordResponse = asyncIntegrationService.sendEmail(request.email());
        return ResponseEntity.ok(new GenericResponse<Boolean>(201, forgetPasswordResponse.message(), forgetPasswordResponse.sended()));
    }

    @PostMapping("/validateCodePassword")
    public ResponseEntity<GenericResponse> postMethodName(@RequestBody ValidateCodePasswordRequest entity) {
        GenericResponse<Boolean> validateCodePassword = authService.validateCodePassword(entity);
        return ResponseEntity.ok(validateCodePassword);
    }
    
    @PostMapping("/resetPassword")
    public ResponseEntity<GenericResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        GenericResponse<Boolean> resetResponse = authService.resetPassword(request);
        return ResponseEntity.ok(resetResponse);
    }
    

}
