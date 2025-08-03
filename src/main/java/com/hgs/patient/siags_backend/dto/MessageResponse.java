package com.hgs.patient.siags_backend.dto;

/**
 * DTO pour une réponse de message simple.
 * Utile pour retourner des messages d'erreur ou de succès.
 */
public class MessageResponse {
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
