package community.whatever.onembackendjava;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class UrlMappingManager {
    private final Map<String, String> shortenUrls = new ConcurrentHashMap<>();
    private final Set<String> blockedDomains = ConcurrentHashMap.newKeySet();

    public String find(String key) {
        return shortenUrls.get(key);
    }

    public boolean putIfAbsent(String key, String url) {
        return shortenUrls.putIfAbsent(key, url) == null;
    }

    public Map<String, String> findAll() {
        return new HashMap<>(shortenUrls);
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
