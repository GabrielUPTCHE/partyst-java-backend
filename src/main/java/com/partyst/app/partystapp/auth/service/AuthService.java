package com.partyst.app.partystapp.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;

import com.partyst.app.partystapp.auth.repository.Token;
import com.partyst.app.partystapp.auth.repository.TokenRepository;
import com.partyst.app.partystapp.entities.RecoveryCode;
import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.records.GenericRedis;
import com.partyst.app.partystapp.records.GenericResponse;
import com.partyst.app.partystapp.records.requests.LoginRequest;
import com.partyst.app.partystapp.records.requests.RegisterRequest;
import com.partyst.app.partystapp.records.requests.ValidateCodePasswordRequest;
import com.partyst.app.partystapp.records.responses.TokenResponse;
import com.partyst.app.partystapp.repositories.RecoveryCodeRepository;
import com.partyst.app.partystapp.repositories.UserRepository;
import com.partyst.app.partystapp.services.RedisQueueService;

@Service
public class AuthService {

    @Autowired
    private RedisQueueService redisQueueService;
    
    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RecoveryCodeRepository recoveryCodeRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    public GenericResponse<Boolean> validateCodePassword(ValidateCodePasswordRequest request){
        RecoveryCode recoveryCode = recoveryCodeRepository.findByEmail(request.email()).orElse(null);
        if (recoveryCode == null) {
            return new GenericResponse<Boolean>(204, "El email no esta registrado", false);
        }
        if (request.email().equals(recoveryCode.getEmail()) &&  request.code().equals(recoveryCode.getTokenRecovery())) {
            return new GenericResponse<Boolean>(200, "El codigo de confirmacion es correcto", true);
        }
        return new GenericResponse<Boolean>(204, "El codigo es incorrecto", false);
    }

    public User buildUserRegister(RegisterRequest request) {
        User user = User.builder()
           .name(request.name())
           .email(request.email())
           .password(passwordEncoder.encode(request.password()))
           .lastname(request.lastname())
           .nickname(request.nickname())
           .cellphone(request.celphone())
         .build();
         return user;
    }

    public TokenResponse register(RegisterRequest request) {
        User user = buildUserRegister(request);
        try {
        User savedUser = userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return new TokenResponse(jwtToken, refreshToken);
        } catch (Exception e) {   
            
            System.out.println("⚠ Se añade a cola Redis LIST");
            redisQueueService.enqueue(
                new GenericRedis<>("REGISTER_REQUEST", user)
            );
            String jwtToken = "asdsafdsad";
            String refreshToken = "sadasfsafas";
            return new TokenResponse(jwtToken, refreshToken);

        }
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
