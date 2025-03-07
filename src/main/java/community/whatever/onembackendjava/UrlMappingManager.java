package community.whatever.onembackendjava;

import community.whatever.onembackendjava.constant.UrlConstants;
import community.whatever.onembackendjava.dto.ShortenUrlWithExpiryInfo;
import org.springframework.stereotype.Component;

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
        
        return mapping.originalUrl();
    }

    public boolean putIfAbsent(String key, String url) {
        return putIfAbsent(key, url, UrlConstants.DEFAULT_TTL_MINUTES);
    }
    
    public boolean putIfAbsent(String key, String url, Long ttlMinutes) {
        UrlMapping newMapping = new UrlMapping(url, ttlMinutes);
        
        return shortenUrls.putIfAbsent(key, newMapping) == null;
    }

    /**
     * 유효한(만료되지 않은) URL 매핑만 반환합니다.
     * 
     * @return 유효한 URL 매핑만 포함한 Map (key: 단축 URL 키, value: 원본 URL)
     */
    public Map<String, String> findValidUrls() {
        return shortenUrls.entrySet().stream()
                .filter(entry -> !entry.getValue().isExpired())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().originalUrl(),
                    (existing, replacement) -> existing,
                    HashMap::new
                ));
    }
    
    /**
     * 만료 여부와 상관없이 모든 URL 매핑을 반환합니다.
     * 
     * @return 모든 URL 매핑을 포함한 Map (key: 단축 URL 키, value: 원본 URL)
     */
    public Map<String, ShortenUrlWithExpiryInfo> findAllUrls() {
        return shortenUrls.entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> new ShortenUrlWithExpiryInfo(entry.getValue().originalUrl(), entry.getValue().expiryTime()),
                    (existing, replacement) -> existing,
                    HashMap::new
                ));
    }
    
    /**
     * 만료된 URL 매핑을 모두 삭제합니다. 관리자 기능으로 사용됩니다.
     */
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
