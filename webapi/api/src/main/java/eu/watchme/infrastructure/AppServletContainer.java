package eu.watchme.infrastructure;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AppServletContainer extends ServletContainer {
	private final static String sRequestDelimiter = "-------------------------------------------------------------------------------------------------------------------------------";
	private Logger logger = LoggerFactory.getLogger(AppServletContainer.class);
	Marker mMarker = MarkerFactory.getMarker("TX");

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long startTime = System.currentTimeMillis();

		try {
			logger.info(mMarker, "Thread-{} : Starting request {} for {} on {}\n{}", Thread.currentThread().getId(), request.getRemoteAddr(),
					request.getMethod(), request.getRequestURI(), sRequestDelimiter);
			super.service(request, response);
		} finally {
			logger.info(mMarker, "Thread-{} : Request {} for {} on {} in {}ms - statusCode {}\n{}", Thread.currentThread().getId(), request.getRemoteAddr(),
					request.getMethod(), request.getRequestURI(), System.currentTimeMillis() - startTime, response.getStatus(), sRequestDelimiter);
		}
	}
}
