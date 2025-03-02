package community.whatever.onembackendjava.common.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Profile("default")
@Component
public class RequestLoggingFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {

		if (request instanceof HttpServletRequest httpRequest
			&& response instanceof HttpServletResponse httpResponse) { // java16
			ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
			ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

			chain.doFilter(wrappedRequest, wrappedResponse);

			logRequest(wrappedRequest);
			logResponse(wrappedResponse);
			wrappedResponse.copyBodyToResponse();
		} else {
			chain.doFilter(request, response);
		}
	}

	private void logRequest(ContentCachingRequestWrapper request) {
		String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
		logger.info("Request: {} {} , Host: {}\n Body: {}", request.getMethod(), request.getRequestURI(),
			request.getRemoteHost(), requestBody);
	}

	private void logResponse(ContentCachingResponseWrapper response) throws IOException {
		String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
		logger.info("Response: Status {} | Body: {}", response.getStatus(), responseBody);
	}
}