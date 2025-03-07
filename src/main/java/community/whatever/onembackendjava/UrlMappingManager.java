package community.whatever.onembackendjava;

import community.whatever.onembackendjava.constant.UrlConstants;
import community.whatever.onembackendjava.dto.ShortenUrlWithExpiryInfo;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class UrlMappingManager {

    private final UrlMappings urlMappings = new UrlMappings();
    private final Set<String> blockedDomains = ConcurrentHashMap.newKeySet();
    
    public String find(String key) {
        return urlMappings.find(key);
    }

    public boolean putIfAbsent(String key, String url) {
        return putIfAbsent(key, url, UrlConstants.DEFAULT_TTL_MINUTES);
    }
    
    public boolean putIfAbsent(String key, String url, Long ttlMinutes) {
        return urlMappings.putIfAbsent(key, url, ttlMinutes);
    }

    /**
     * 유효한(만료되지 않은) URL 매핑만 반환합니다.
     * 
     * @return 유효한 URL 매핑만 포함한 Map (key: 단축 URL 키, value: 원본 URL)
     */
    public Map<String, String> findValidUrls() {
        return urlMappings.findValid();
    }
    
    /**
     * 만료 여부와 상관없이 모든 URL 매핑을 반환합니다.
     * 
     * @return 모든 URL 매핑을 포함한 Map (key: 단축 URL 키, value: 원본 URL)
     */
    public Map<String, ShortenUrlWithExpiryInfo> findAllUrls() {
        return urlMappings.findAll();
    }
    
    /**
     * 만료된 URL 매핑을 모두 삭제합니다. 관리자 기능으로 사용됩니다.
     */
    public void cleanExpiredUrls() {
        urlMappings.cleanExpired();
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
