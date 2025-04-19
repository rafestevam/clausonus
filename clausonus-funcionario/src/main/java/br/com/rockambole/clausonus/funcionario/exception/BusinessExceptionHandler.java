package br.com.rockambole.clausonus.funcionario.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Handler específico para tratar exceções de negócio
 */
@Provider
public class BusinessExceptionHandler implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException exception) {
        FuncionarioExceptionHandler.ErrorMessage errorMessage = 
                new FuncionarioExceptionHandler.ErrorMessage(
                    exception.getStatus().getStatusCode(),
                    exception.getMessage(),
                    "Erro de negócio");
        
        return Response.status(exception.getStatus())
                .entity(errorMessage)
                .build();
    }
}