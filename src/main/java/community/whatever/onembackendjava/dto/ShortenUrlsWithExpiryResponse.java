package community.whatever.onembackendjava.dto;

import java.util.Map;


public record ShortenUrlsWithExpiryResponse(Map<String, ShortenUrlWithExpiryInfo> shortenUrlsMap) {
}
