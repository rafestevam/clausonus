package br.com.rockambole.clausonus.funcionario.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.stream.Collectors;

/**
 * Handler específico para tratar exceções de validação de constraints
 */
@Provider
public class ConstraintViolationExceptionHandler implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        // Extrair mensagens de violação
        String violations = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
                
        FuncionarioExceptionHandler.ErrorMessage errorMessage = 
                new FuncionarioExceptionHandler.ErrorMessage(
                    Response.Status.BAD_REQUEST.getStatusCode(),
                    "Violações de validação: " + violations,
                    "Os dados fornecidos não passaram na validação");
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMessage)
                .build();
    }
}