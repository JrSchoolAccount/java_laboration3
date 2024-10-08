import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ProductValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {


    @Override
    public Response toResponse(ConstraintViolationException exception) {
        StringBuilder errorMessage = new StringBuilder("Validation errors: ");
        exception.getConstraintViolations().forEach(violation ->
                errorMessage.append(violation.getMessage()).append("; ")
        );

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(errorMessage.toString())
                .build();
    }
}
