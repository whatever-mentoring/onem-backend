package community.whatever.onembackendjava;

import java.time.Instant;

record UrlMapping(String originalUrl, Instant expiryTime) {

    public UrlMapping(String originalUrl, Long ttlMinutes) {
        this(originalUrl, calculateExpiryTime(ttlMinutes));
    }
    
    private static Instant calculateExpiryTime(Long ttlMinutes) {
        long ttlSeconds = ttlMinutes * 60;
        return Instant.now().plusSeconds(ttlSeconds);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiryTime);
    }
}
