package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.UrlMappingManager;
import community.whatever.onembackendjava.constant.UrlConstants;
import community.whatever.onembackendjava.dto.*;
import community.whatever.onembackendjava.exception.UrlShortenException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class UrlShortenService {
    private final UrlMappingManager urlMappingManager;

    private static final int KEY_LENGTH = 6;

    public SearchShortenUrlResponse searchShortenUrl(SearchShortenUrlRequest request) {
        String url = urlMappingManager.find(request.key());
        if (url == null) {
            throw UrlShortenException.notFound(request.key());
        }
        return new SearchShortenUrlResponse(url);
    }

    public CreateShortenUrlResponse createShortenUrl(CreateShortenUrlRequest request) {
        String originUrl = request.originUrl();
        if (!originUrl.startsWith("http://") && !originUrl.startsWith("https://")) {
            originUrl = "https://" + originUrl;
        }
        
        validateUrl(originUrl);

        URI uri = getUri(originUrl);

        String host = uri.getHost();
        if (host != null && urlMappingManager.isUrlBlocked(host)) {
            throw UrlShortenException.blockedDomain(host);
        }

        long ttlMinutes = request.ttlMinutes() != null ? request.ttlMinutes() : UrlConstants.DEFAULT_TTL_MINUTES;

        String randomKey;
        boolean success;
        do {
            randomKey = generateRandomKey();
            success = urlMappingManager.putIfAbsent(randomKey, originUrl, ttlMinutes);
        } while (!success);

        return new CreateShortenUrlResponse(randomKey);
    }

    private URI getUri(String originUrl) {
        URI uri;
        try {
            uri = new URI(originUrl);
        } catch (URISyntaxException e) {
            throw UrlShortenException.invalidUrl(e.getMessage());
        }
        return uri;
    }

    private void validateUrl(String url) {
        URI uri = getUri(url);
        String scheme = uri.getScheme();
        if (scheme == null) {
            throw UrlShortenException.invalidUrl(UrlConstants.URL_MUST_HAVE_SCHEME);
        }

        if (!scheme.equals("https") && !scheme.equals("http")) {
            throw UrlShortenException.invalidUrl(UrlConstants.ONLY_HTTP_HTTPS_ALLOWED);
        }

        String host = uri.getHost();
        if (host == null || host.isEmpty()) {
            throw UrlShortenException.invalidUrl(UrlConstants.URL_MUST_HAVE_VALID_HOST);
        }
    }

    private String generateRandomKey() {
        long timestamp = Instant.now().toEpochMilli();
        long random = ThreadLocalRandom.current().nextLong();
        String combined = timestamp + ":" + random;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            String encoded = Base64.getUrlEncoder().encodeToString(hash);
            return encoded.substring(0, KEY_LENGTH);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 알고리즘을 사용할 수 없습니다: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    public String getOriginalUrl(String code) {
        String url = urlMappingManager.find(code);
        if (url == null) {
            throw UrlShortenException.notFound(code);
        }
        return url;
    }
    

    @Scheduled(fixedRate = 3600000) // 1시간(3600000 밀리초)마다 실행
    public void cleanupExpiredUrls() {
        urlMappingManager.cleanExpiredUrls();
    }
}
