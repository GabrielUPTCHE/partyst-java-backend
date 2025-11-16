package com.partyst.app.partystapp.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.partyst.app.partystapp.auth.repository.Token;
import com.partyst.app.partystapp.auth.repository.TokenRepository;
import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.requests.LoginRequest;
import com.partyst.app.partystapp.records.requests.RegisterRequest;
import com.partyst.app.partystapp.records.responses.TokenResponse;
import com.partyst.app.partystapp.repositories.UserRepository;

@Service
public class AuthService {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

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
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userRepository.findByEmail(request.email()).orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user); 
        /* revokeAllUserTokens(user); */
        saveUserToken(user, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
    }

    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
            .token(jwtToken)
            .tokenType(Token.TokenType.BEARER)
            .expired(false)
            .revoked(false)
            .build();
        tokenService.saveToken(token);
    }

   /*  private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenService.
    } */

    public TokenResponse refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Bearer token");
        }
        String refreshToken = authHeader.substring(7);
        String userEmail = jwtService.extractUserName(refreshToken);
        if (userEmail == null) {
            throw new IllegalArgumentException("Invalid Refresh token");
        }
        
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new UsernameNotFoundException(userEmail));
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new IllegalArgumentException("Invalid Refresh token");
        }
        String accesToken = jwtService.generateToken(user);
        /* revokeAllUserTokens(user); */
        saveUserToken(user, accesToken);
        return new  TokenResponse(accesToken, refreshToken);
    }
}
