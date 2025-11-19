package com.partyst.app.partystapp.config;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.partyst.app.partystapp.auth.repository.Token;
import com.partyst.app.partystapp.auth.repository.TokenRepository;
import com.partyst.app.partystapp.auth.service.JwtService;
import com.partyst.app.partystapp.entities.User;
import com.partyst.app.partystapp.repositories.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter{
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        
        logger.info("Processing request: {} {}", request.getMethod(), request.getServletPath());
        
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        
        if (request.getServletPath().contains("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        logger.info("Authorization Header: {}", authHeader);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("No Bearer token found or invalid format");
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header");
            return;
        }
        
        String jwtToken = authHeader.substring(7);
        logger.info("JWT Token extracted");
        
        try {
            String userEmail = jwtService.extractUserName(jwtToken);
            logger.info("User email from token: {}", userEmail);
            
            if (userEmail == null) {
                logger.warn("Could not extract user email from token");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: cannot extract user email");
                return;
            }
            
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                logger.info("User already authenticated");
                filterChain.doFilter(request, response);
                return;
            }
            
            Token token = tokenRepository.findByToken(jwtToken).orElse(null);
            if (token == null) {
                logger.warn("Token not found in database");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token not found");
                return;
            }
            
            if (token.isExpired() || token.isRevoked()) {
                logger.warn("Token is expired or revoked");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token expired or revoked");
                return;
            }
            
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            Optional<User> user = userRepository.findByEmail(userDetails.getUsername());
            
            if (user.isEmpty()) {
                logger.warn("User not found in database: {}", userEmail);
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                return;
            } 

            boolean isTokenValid = jwtService.isTokenValid(jwtToken, user.get());
            if (!isTokenValid) {
                logger.warn("Token validation failed");
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
            
            var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            logger.info("User authenticated successfully: {}", userEmail);
            
            filterChain.doFilter(request, response);
            
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            logger.error("Error during JWT authentication: {}", e.getMessage());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: " + e.getMessage());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}