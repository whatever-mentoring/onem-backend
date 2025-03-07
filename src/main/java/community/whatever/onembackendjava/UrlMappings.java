package community.whatever.onembackendjava;

import community.whatever.onembackendjava.dto.ShortenUrlWithExpiryInfo;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class UrlMappings {
    private final Map<String, UrlMapping> mappings;

    /**
     * URL 매핑 정보를 저장하는 내부 레코드
     */
    public record UrlMapping(String originalUrl, Instant expiryTime) {

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

    public UrlMappings() {
        this.mappings = new ConcurrentHashMap<>();
    }

    public String find(String key) {
        UrlMapping mapping = mappings.get(key);
        if (mapping == null) {
            return null;
        }
        if (mapping.isExpired()) {
            mappings.remove(key);
            return null;
        }
        return mapping.originalUrl();
    }

    /**
     * 키가 존재하지 않을 경우에만 URL 매핑을 추가합니다.
     *
     * @return 추가 성공 여부
     */
    public boolean putIfAbsent(String key, String url, Long ttlMinutes) {
        UrlMapping newMapping = new UrlMapping(url, ttlMinutes);
        return mappings.putIfAbsent(key, newMapping) == null;
    }

    /**
     * 모든 URL 매핑을 반환합니다 (만료 여부에 상관없음).
     */
    public Map<String, ShortenUrlWithExpiryInfo> findAll() {
        return mappings.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ShortenUrlWithExpiryInfo(entry.getValue().originalUrl(), entry.getValue().expiryTime()),
                        (existing, replacement) -> existing,
                        HashMap::new
                ));
    }

    /**
     * 유효한(만료되지 않은) URL 매핑만 반환합니다.
     */
    public Map<String, String> findValid() {
        return mappings.entrySet().stream()
                .filter(entry -> !entry.getValue().isExpired())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().originalUrl(),
                        (existing, replacement) -> existing,
                        HashMap::new
                ));
    }

    /**
     * 만료된 URL 매핑을 모두 삭제합니다.
     */
    public void cleanExpired() {
        mappings.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * 매핑의 크기를 반환합니다.
     */
    public int size() {
        return mappings.size();
    }

}
