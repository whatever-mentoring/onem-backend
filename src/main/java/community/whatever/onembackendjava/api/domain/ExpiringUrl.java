package community.whatever.onembackendjava.api.domain;

import java.time.LocalDateTime;

public class ExpiringUrl {
    private final String originalUrl;
    private final LocalDateTime expiryTime;

    public ExpiringUrl(String originalUrl, LocalDateTime expiryTime) {
        this.originalUrl = originalUrl;
        this.expiryTime = expiryTime;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public String getOriginalUrl() {
        return originalUrl;
    }
}

