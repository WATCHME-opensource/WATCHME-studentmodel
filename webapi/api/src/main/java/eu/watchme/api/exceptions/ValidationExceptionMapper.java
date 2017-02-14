package eu.watchme.api.exceptions;

import eu.watchme.modules.domainmodel.exceptions.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ValidationException> {
	private Logger logger = LoggerFactory.getLogger(ValidationExceptionMapper.class);

	@Override
	public Response toResponse(ValidationException ex) {
		logger.error("Thread-{}", Thread.currentThread().getId(), ex);
		ErrorMessage errorMessage = new ErrorMessage(ex);
		return Response.status(errorMessage.getStatus()).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
	}
}
