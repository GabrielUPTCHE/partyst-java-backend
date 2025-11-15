package com.partyst.app.partystapp.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.auth.controller.LoginRequest;
import com.partyst.app.partystapp.auth.controller.RegisterRequest;
import com.partyst.app.partystapp.auth.controller.TokenResponse;
import com.partyst.app.partystapp.auth.repository.Token;
import com.partyst.app.partystapp.auth.repository.TokenRepository;
import com.partyst.app.partystapp.usuario.User;
import com.partyst.app.partystapp.usuario.UserRepository;

@Service
public class AuthService {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public TokenResponse register(RegisterRequest request) {
        User user = User.builder()
            .name(request.name())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .lastname(request.lastname())
            .birthdate(request.birthdate())
            .celphone(request.celphone())
            .build();
        User savedUser = userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    public TokenResponse login(LoginRequest request) {
        return null;
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
            .token(jwtToken)
            .tokenType(Token.TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
            tokenRepository.save(token);
    }

    public TokenResponse refreshToken(String authHeader) {
        return null;
    }
}
