package community.whatever.onembackendjava.exception;

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
        this(message, status, "URL_ERROR");
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    // 자주 사용되는 예외에 대한 팩토리 메서드
    public static UrlShortenException invalidUrl(String message) {
        return new UrlShortenException(message, HttpStatus.BAD_REQUEST, "INVALID_URL");
    }
    
    public static UrlShortenException blockedDomain(String domain) {
        return new UrlShortenException(
            "The domain '" + domain + "' has been blocked by administrator", 
            HttpStatus.FORBIDDEN, 
            "BLOCKED_DOMAIN"
        );
    }
    
    public static UrlShortenException notFound(String key) {
        return new UrlShortenException(
            "No URL found for key: " + key, 
            HttpStatus.NOT_FOUND, 
            "URL_NOT_FOUND"
        );
    }
}
