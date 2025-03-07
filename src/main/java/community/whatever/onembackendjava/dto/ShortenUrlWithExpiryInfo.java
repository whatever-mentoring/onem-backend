package community.whatever.onembackendjava.dto;

import java.time.Instant;

public record ShortenUrlWithExpiryInfo(String originalUrl, Instant expiryTime, boolean expired) {


    public ShortenUrlWithExpiryInfo(String originalUrl, Instant expiryTime) {
        this(originalUrl, expiryTime, Instant.now().isAfter(expiryTime));
    }
}