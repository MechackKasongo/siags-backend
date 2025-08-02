package com.hgs.patient.siags_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // Par défaut, renvoie une erreur 500
public class OdooIntegrationException extends RuntimeException {

    public OdooIntegrationException(String message) {
        super(message);
    }

    public OdooIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
