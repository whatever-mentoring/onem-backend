package community.whatever.onembackendjava.constant;

/**
 * URL 관련 상수들을 정의하는 클래스
 */
public final class UrlConstants {
    
    private UrlConstants() {}

    // URL 검증 관련 메시지
    public static final String URL_MUST_HAVE_SCHEME = "URL은 스키마(http 또는 https)를 포함해야 합니다";
    public static final String ONLY_HTTP_HTTPS_ALLOWED = "http와 https 스키마만 허용됩니다";
    public static final String URL_MUST_HAVE_VALID_HOST = "URL은 유효한 호스트를 포함해야 합니다";
    public static final String URL_MUST_NOT_BE_EMPTY = "URL은 비어있을 수 없습니다";
    public static final String INVALID_URL_FORMAT = "잘못된 URL 형식: ";
    
    // 도메인 블랙리스트 관련 메시지
    public static final String DOMAIN_BLOCKED_BY_ADMIN = "도메인 '%s'는 관리자에 의해 차단되었습니다";
    public static final String NO_URL_FOUND_FOR_KEY = "키 '%s'에 해당하는 URL을 찾을 수 없습니다";
}
