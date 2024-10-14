package org.laboration3.exceptions;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ProductValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    private static final Logger logger = LoggerFactory.getLogger(ProductValidationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        StringBuilder errorMessage = new StringBuilder("Validation errors: ");
        exception.getConstraintViolations().forEach(violation ->
                errorMessage.append(violation.getMessage()).append("; ")
        );

        logger.error("{}", errorMessage);
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMessage.toString())
                .build();
    }
}
