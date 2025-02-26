package community.whatever.onembackendjava;

import community.whatever.onembackendjava.dto.*;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class UrlShortenService {
    private final Map<String, String> shortenUrls = new ConcurrentHashMap<>();

    private static final int KEY_LENGTH = 6;

    public SearchShortenUrlResponse searchShortenUrl(SearchShortenUrlRequest request) {
        String url = shortenUrls.get(request.key());
        if (url == null) {
            throw new IllegalArgumentException("Invalid key");
        }
        return new SearchShortenUrlResponse(url);
    }

    public CreateShortenUrlResponse createShortenUrl(CreateShortenUrlRequest request) {
        String randomKey;
        String originUrl = request.originUrl();

        do {
            randomKey = generateRandomKey();
        } while (shortenUrls.putIfAbsent(randomKey, originUrl) != null);

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

    public HashMapResponse getAllShortenUrls() {
        return new HashMapResponse(new HashMap<>(shortenUrls));
    }

    public String addToShortenUrls(PostShortenUrlsRequest request) {
        if (request.shortenUrls() != null) {
            request.shortenUrls().forEach(shortenUrls::putIfAbsent);
        }
        return "Success";
    }
}
