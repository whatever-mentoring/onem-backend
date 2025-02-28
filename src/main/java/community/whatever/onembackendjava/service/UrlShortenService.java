package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.UrlMappingManager;
import community.whatever.onembackendjava.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            throw new IllegalArgumentException("Invalid key");
        }
        return new SearchShortenUrlResponse(url);
    }

    public CreateShortenUrlResponse createShortenUrl(CreateShortenUrlRequest request) {
        String originUrl = request.originUrl();
        
        if (urlMappingManager.isUrlBlocked(originUrl)) {
            throw new IllegalArgumentException("이 url은 차단되었습니다.");
        }
        
        String randomKey;
        do {
            randomKey = generateRandomKey();
        } while (!urlMappingManager.putIfAbsent(randomKey, originUrl));

        return new CreateShortenUrlResponse(randomKey);
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
        return urlMappingManager.find(code);
    }
}
