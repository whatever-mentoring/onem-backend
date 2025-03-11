package community.whatever.onembackendjava.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringJoiner;

/**
 * 모든 HTTP 요청과 응답을 로깅하는 필터
 */
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            String requestMethod = request.getMethod();
            String requestUri = request.getRequestURI();
            int statusCode = response.getStatus();

            // 기본 요청 정보 로깅
            log.info("REQUEST: {} {} | Status: {} | Duration: {}ms | IP: {}",
                    requestMethod,
                    requestUri,
                    statusCode,
                    duration,
                    getClientIp(request)
            );

            // 쿼리 파라미터 개별 로깅
            Map<String, String[]> queryParams = request.getParameterMap();
            if (!queryParams.isEmpty()) {
                log.info("Query Parameters:");
                queryParams.forEach((key, values) -> {
                    if (values.length == 1) {
                        log.info("  {} = {}", key, values[0]);
                    } else {
                        StringJoiner valueJoiner = new StringJoiner(", ", "[", "]");
                        for (String value : values) {
                            valueJoiner.add(value);
                        }
                        log.info("  {} = {}", key, valueJoiner.toString());
                    }
                });
            }

            // 요청 헤더 로깅
            log.info("Request Headers:");
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                log.info("  {} = {}", headerName, request.getHeader(headerName));
            }

            // POST/PUT/PATCH 요청의 본문 로깅
            if (requestMethod.equals("POST") || requestMethod.equals("PUT") || requestMethod.equals("PATCH")) {
                byte[] content = requestWrapper.getContentAsByteArray();
                if (content.length > 0) {
                    String requestBody = new String(content);
                    log.info("Request Body: {}", requestBody);
                }
            }
        }
    }

    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
}
