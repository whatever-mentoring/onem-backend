package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.UrlMappingManager;
import community.whatever.onembackendjava.dto.*;
import community.whatever.onembackendjava.exception.UrlShortenException;
import lombok.RequiredArgsConstructor;
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
        
        if (urlMappingManager.isUrlBlocked(originUrl)) {
            try {
                URI uri = new URI(originUrl);
                String host = uri.getHost();
                throw UrlShortenException.blockedDomain(host);
            } catch (URISyntaxException e) {
                throw UrlShortenException.invalidUrl("Invalid URL format: " + e.getMessage());
            }
        }
        
        String randomKey;
        do {
            randomKey = generateRandomKey();
        } while (!urlMappingManager.putIfAbsent(randomKey, originUrl));

        return new CreateShortenUrlResponse(randomKey);
    }

    private void validateUrl(String url) {
        try {
            URI uri = new URI(url);
            
            // 스키마 검증
            String scheme = uri.getScheme();
            if (scheme == null) {
                throw UrlShortenException.invalidUrl("URL must have a scheme (http or https)");
            }
            
            if (!scheme.equals("http") && !scheme.equals("https")) {
                throw UrlShortenException.invalidUrl("Only http and https schemes are allowed");
            }
            
            // 호스트 검증
            String host = uri.getHost();
            if (host == null || host.isEmpty()) {
                throw UrlShortenException.invalidUrl("URL must have a valid host");
            }
            
        } catch (URISyntaxException e) {
            throw UrlShortenException.invalidUrl("Invalid URL format: " + e.getMessage());
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
}
