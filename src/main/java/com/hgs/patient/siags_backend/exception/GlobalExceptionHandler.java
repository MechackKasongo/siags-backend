package com.hgs.patient.siags_backend.exception;

import com.hgs.patient.siags_backend.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // Rend cette classe capable de gérer les exceptions de tous les contrôleurs
public class GlobalExceptionHandler {
    // Gère les erreurs de validation causées par @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),

                "Validation Failed",
                "Les données envoyées sont invalides.",
                request.getDescription(false).replace("uri=", ""),
                errors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Gère les exceptions de ressource non trouvée (ResourceNotFoundException)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),

                "Not Found",
                ex.getMessage(), // Utilise le message spécifique de l'exception (ex: "Patient non trouvé...")
                request.getDescription(false).replace("uri=", ""),
                null // Pas d'erreurs de champs spécifiques pour ce type d'erreur
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Gère les exceptions génériques non gérées spécifiquement
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),

                "Internal Server Error",
                "Une erreur inattendue est survenue: " + ex.getMessage(),
                request.getDescription(false).replace("uri=", ""),
                null // Pas d'erreurs de champs spécifiques ici
        );

        // Pour la production, remplacez ceci par un vrai logger (ex: SLF4J/Logback)
        ex.printStackTrace();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
