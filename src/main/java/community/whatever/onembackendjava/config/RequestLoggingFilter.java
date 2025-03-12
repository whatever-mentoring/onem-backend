package community.whatever.onembackendjava.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

/**
 * 모든 HTTP 요청과 응답을 로깅하는 필터
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private final RequestLogger requestLogger;

    public RequestLoggingFilter(RequestLogger requestLogger) {
        this.requestLogger = requestLogger;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            requestLogger.logRequest(request, requestWrapper, response, duration);
        }
    }
}
