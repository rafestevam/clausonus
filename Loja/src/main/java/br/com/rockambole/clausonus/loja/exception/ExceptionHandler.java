package br.com.rockambole.clausonus.loja.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof NegocioException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(buildErrorMessage(exception.getMessage()))
                    .build();
        } else if (exception instanceof ConstraintViolationException) {
            return handleConstraintViolation((ConstraintViolationException) exception);
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(buildErrorMessage("Erro interno do servidor. Por favor, contacte o suporte."))
                    .build();
        }
    }

    private Response handleConstraintViolation(ConstraintViolationException exception) {
        Map<String, String> errors = exception.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> getPropertyName(violation),
                        ConstraintViolation::getMessage,
                        (error1, error2) -> error1 + ", " + error2
                ));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errors)
                .build();
    }

    private String getPropertyName(ConstraintViolation<?> violation) {
        String propertyPath = violation.getPropertyPath().toString();
        String[] parts = propertyPath.split("\\.");
        return parts.length > 0 ? parts[parts.length - 1] : propertyPath;
    }

    private Map<String, String> buildErrorMessage(String message) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("mensagem", message);
        return errorMap;
    }
}
