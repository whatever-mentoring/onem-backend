package community.whatever.onembackendjava.exception;

import community.whatever.onembackendjava.constant.ErrorCode;
import community.whatever.onembackendjava.constant.UrlConstants;
import org.springframework.http.HttpStatus;

public class UrlShortenException extends RuntimeException {
    
    private final HttpStatus status;
    private final String errorCode;
    
    public UrlShortenException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
    
    public UrlShortenException(String message, HttpStatus status) {
        this(message, status, ErrorCode.URL_ERROR);
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    // 자주 사용되는 예외에 대한 팩토리 메서드
    public static UrlShortenException invalidUrl(String message) {
        return new UrlShortenException(message, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_URL);
    }
    
    public static UrlShortenException blockedDomain(String domain) {
        return new UrlShortenException(
            String.format(UrlConstants.DOMAIN_BLOCKED_BY_ADMIN, domain), 
            HttpStatus.FORBIDDEN, 
            ErrorCode.BLOCKED_DOMAIN
        );
    }
    
    public static UrlShortenException notFound(String key) {
        return new UrlShortenException(
            String.format(UrlConstants.NO_URL_FOUND_FOR_KEY, key), 
            HttpStatus.NOT_FOUND, 
            ErrorCode.URL_NOT_FOUND
        );
    }
}
