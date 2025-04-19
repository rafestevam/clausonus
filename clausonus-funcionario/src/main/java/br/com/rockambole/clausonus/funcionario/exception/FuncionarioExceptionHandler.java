package br.com.rockambole.clausonus.funcionario.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

/**
 * Handler para tratar exceções relacionadas ao módulo de Funcionario
 */
@Slf4j
@Provider
public class FuncionarioExceptionHandler implements ExceptionMapper<Exception> {

    /**
     * Classe interna para representar mensagens de erro
     */
    public static class ErrorMessage {
        private final int status;
        private final String message;
        private final String developerMessage;

        public ErrorMessage(int status, String message, String developerMessage) {
            this.status = status;
            this.message = message;
            this.developerMessage = developerMessage;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public String getDeveloperMessage() {
            return developerMessage;
        }
    }

    @Override
    public Response toResponse(Exception exception) {
        log.error("Erro ao processar requisição: ", exception);
        
        if (exception instanceof NotFoundException) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage(
                            Response.Status.NOT_FOUND.getStatusCode(),
                            exception.getMessage(),
                            "O recurso solicitado não foi encontrado"))
                    .build();
        }
        
        if (exception instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage(
                            Response.Status.BAD_REQUEST.getStatusCode(),
                            exception.getMessage(),
                            "Problema nos dados informados"))
                    .build();
        }
        
        // Tratamento para validações de bean
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) exception;
            String violations = cve.getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
                    
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage(
                            Response.Status.BAD_REQUEST.getStatusCode(),
                            "Violações de validação: " + violations,
                            "Os dados fornecidos não passaram na validação"))
                    .build();
        }
        
        // Para exceções não mapeadas
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorMessage(
                        Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                        "Ocorreu um erro interno no sistema",
                        exception.getMessage()))
                .build();
    }
}
