package community.whatever.onembackendjava;

import java.time.Instant;

record UrlMapping(String originalUrl, Instant expiryTime) {

    public boolean isExpired() {
        return Instant.now().isAfter(expiryTime);
    }
}
