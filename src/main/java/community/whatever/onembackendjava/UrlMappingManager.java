package community.whatever.onembackendjava;

import community.whatever.onembackendjava.constant.UrlConstants;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class UrlMappingManager {
    private static class UrlMapping {
        private final String originalUrl;
        private final Instant expiryTime;
        
        public UrlMapping(String originalUrl, Instant expiryTime) {
            this.originalUrl = originalUrl;
            this.expiryTime = expiryTime;
        }
        
        public String getOriginalUrl() {
            return originalUrl;
        }
        
        public Instant getExpiryTime() {
            return expiryTime;
        }
        
        public boolean isExpired() {
            return Instant.now().isAfter(expiryTime);
        }
    }

    private final Map<String, UrlMapping> shortenUrls = new ConcurrentHashMap<>();
    private final Set<String> blockedDomains = ConcurrentHashMap.newKeySet();
    
    public String find(String key) {
        UrlMapping mapping = shortenUrls.get(key);
        
        if (mapping == null || mapping.isExpired()) {
            if (mapping != null && mapping.isExpired()) {
                shortenUrls.remove(key);
            }
            return null;
        }
        
        return mapping.getOriginalUrl();
    }

    public boolean putIfAbsent(String key, String url) {
        return putIfAbsent(key, url, UrlConstants.DEFAULT_TTL_MINUTES);
    }
    
    public boolean putIfAbsent(String key, String url, Integer ttlMinutes) {
        long ttlSeconds = ttlMinutes * 60;
        Instant expiryTime = Instant.now().plusSeconds(ttlSeconds);
        UrlMapping newMapping = new UrlMapping(url, expiryTime);
        
        return shortenUrls.putIfAbsent(key, newMapping) == null;
    }

    public Map<String, String> findAll() {
        return shortenUrls.entrySet().stream()
                .filter(entry -> !entry.getValue().isExpired())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().getOriginalUrl(),
                    (existing, replacement) -> existing,
                    HashMap::new
                ));
    }
    
    public void cleanExpiredUrls() {
        shortenUrls.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    public void blockDomain(String domain) {
        blockedDomains.add(normalizeDomain(domain));
    }
    
    public boolean unblockDomain(String domain) {
        return blockedDomains.remove(normalizeDomain(domain));
    }
    
    public Set<String> getBlockedDomains() {
        return new HashSet<>(blockedDomains);
    }
    
    public boolean isUrlBlocked(String url) {
        try {
            URI uri = new URI(url);
            String domain = normalizeDomain(uri.getHost());
            return blockedDomains.contains(domain);
        } catch (URISyntaxException e) {
            return true;
        }
    }
    
    private String normalizeDomain(String domain) {
        if (domain == null) {
            return "";
        }
        
        String normalized = domain.toLowerCase();
        if (normalized.startsWith("www.")) {
            normalized = normalized.substring(4);
        }
        
        return normalized;
    }
}
