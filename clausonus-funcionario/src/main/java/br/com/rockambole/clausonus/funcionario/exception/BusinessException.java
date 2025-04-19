package br.com.rockambole.clausonus.funcionario.exception;

import jakarta.ws.rs.core.Response;

/**
 * Exceção para erros de negócio
 */
public class BusinessException extends RuntimeException {
    
    private final Response.Status status;
    
    public BusinessException(String message) {
        this(message, Response.Status.BAD_REQUEST);
    }
    
    public BusinessException(String message, Response.Status status) {
        super(message);
        this.status = status;
    }
    
    public Response.Status getStatus() {
        return status;
    }
}