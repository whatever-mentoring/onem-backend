package community.whatever.onembackendjava.common.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Profile("default")
@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		return request.getRequestURI().startsWith("/actuator");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

		filterChain.doFilter(wrappedRequest, wrappedResponse);
		logRequest(wrappedRequest);
		logResponse(wrappedResponse);
		wrappedResponse.copyBodyToResponse();
	}

	private void logRequest(ContentCachingRequestWrapper request) {
		String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
		log.info("Request: {} {} , Host: {}\n Body: {}", request.getMethod(), request.getRequestURI(),
			request.getRemoteHost(), requestBody);
	}

	private void logResponse(ContentCachingResponseWrapper response) throws IOException {
		String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
		log.info("Response: Status {} | Body: {}", response.getStatus(), responseBody);
	}

}