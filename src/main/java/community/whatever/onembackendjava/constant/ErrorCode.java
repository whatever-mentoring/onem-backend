package community.whatever.onembackendjava.constant;

/**
 * 에러 코드 상수들을 정의하는 클래스
 */
public final class ErrorCode {
    
    private ErrorCode() {}
    
    public static final String GENERIC_ERROR = "SERVER_ERROR";
    public static final String INVALID_REQUEST = "INVALID_REQUEST";
    
    public static final String URL_ERROR = "URL_ERROR";
    public static final String INVALID_URL = "INVALID_URL";
    public static final String BLOCKED_DOMAIN = "BLOCKED_DOMAIN";
    public static final String URL_NOT_FOUND = "URL_NOT_FOUND";
}
