package com.hgs.patient.siags_backend.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO générique pour renvoyer des messages de réponse simples.
 */
@Data
@AllArgsConstructor
public class MessageResponse {
    private String message;
}
