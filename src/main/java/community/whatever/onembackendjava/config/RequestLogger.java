package community.whatever.onembackendjava.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringJoiner;

/**
 * HTTP 요청 로깅을 담당하는 클래스
 * 로깅 로직을 RequestLoggingFilter에서 분리하여 관리
 */
@Component
public class RequestLogger {

    private static final Logger log = LoggerFactory.getLogger(RequestLogger.class);

    /**
     * 요청 정보 로깅
     */
    public void logRequest(HttpServletRequest request, ContentCachingRequestWrapper requestWrapper,
                          HttpServletResponse response, long duration) {
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
        logQueryParameters(request);
        
        // 헤더 로깅
        logRequestHeaders(request);
        
        // 요청 본문 로깅 (POST, PUT, PATCH 요청인 경우)
        if (requestMethod.equals("POST") || requestMethod.equals("PUT") || requestMethod.equals("PATCH")) {
            logRequestBody(requestWrapper);
        }
    }

    /**
     * 쿼리 파라미터 로깅
     */
    private void logQueryParameters(HttpServletRequest request) {
        Map<String, String[]> queryParams = request.getParameterMap();
        if (!queryParams.isEmpty()) {
            log.info("Query Parameters:");
            queryParams.forEach((key, values) -> {
                String valueStr = (values.length == 1)
                        ? values[0]
                        : Arrays.toString(values);
                log.info("  {} = {}", key, valueStr);
            });
        }
    }
    
    /**
     * 요청 헤더 로깅
     */
    private void logRequestHeaders(HttpServletRequest request) {
        log.info("Request Headers:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("  {} = {}", headerName, request.getHeader(headerName));
        }
    }
    
    /**
     * 요청 본문 로깅
     */
    private void logRequestBody(ContentCachingRequestWrapper requestWrapper) {
        byte[] content = requestWrapper.getContentAsByteArray();
        if (content.length > 0) {
            String requestBody = new String(content);
            log.info("Request Body: {}", requestBody);
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
