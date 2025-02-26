package community.whatever.onembackendjava.dto;

import java.util.Map;

public record PostShortenUrlsRequest(Map<String, String> shortenUrls) {
}
