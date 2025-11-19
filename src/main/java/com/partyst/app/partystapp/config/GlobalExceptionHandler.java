package com.partyst.app.partystapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.partyst.app.partystapp.records.GenericResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Manejador global de excepciones para la aplicación
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de tipo IllegalArgumentException (recursos no encontrados, validaciones)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<GenericResponse<String>> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            HttpServletRequest request) {
        
        logger.error("❌ IllegalArgumentException en {}: {}", request.getRequestURI(), ex.getMessage());
        
        GenericResponse<String> response = new GenericResponse<>(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            null
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Maneja excepciones genéricas no capturadas
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<GenericResponse<String>> handleGenericException(
            Exception ex, 
            HttpServletRequest request) {
        
        logger.error("❌ Error inesperado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        
        GenericResponse<String> response = new GenericResponse<>(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Error interno del servidor",
            null
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Maneja excepciones de tipo RuntimeException
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GenericResponse<String>> handleRuntimeException(
            RuntimeException ex, 
            HttpServletRequest request) {
        
        logger.error("❌ RuntimeException en {}: {}", request.getRequestURI(), ex.getMessage());
        
        GenericResponse<String> response = new GenericResponse<>(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            null
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
