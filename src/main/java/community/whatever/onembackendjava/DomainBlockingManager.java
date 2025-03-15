package community.whatever.onembackendjava;

import community.whatever.onembackendjava.service.BlockedDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@RequiredArgsConstructor
public class DomainBlockingManager {

    private final BlockedDomainService blockedDomainService;
    
    public void blockDomain(String domain) {
        blockedDomainService.blockDomain(normalizeDomain(domain));
    }
    
    public boolean unblockDomain(String domain) {
        return blockedDomainService.unblockDomain(normalizeDomain(domain));
    }
    
    public Set<String> getBlockedDomains() {
        return blockedDomainService.getAllBlockedDomains();
    }
    
    public boolean isUrlBlocked(String url) {
        try {
            URI uri = new URI(url);
            String domain = normalizeDomain(uri.getHost());
            return blockedDomainService.isDomainBlocked(domain);
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
