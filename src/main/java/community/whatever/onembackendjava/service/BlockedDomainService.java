package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.entity.BlockedDomain;
import community.whatever.onembackendjava.repository.BlockedDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BlockedDomainService {
    
    private final BlockedDomainRepository blockedDomainRepository;
    
    public void blockDomain(String domain) {
        BlockedDomain blockedDomain = BlockedDomain.builder()
                .domain(domain)
                .build();
        blockedDomainRepository.save(blockedDomain);
    }
    
    public boolean unblockDomain(String domain) {
        return blockedDomainRepository.delete(domain);
    }
    
    public Set<String> getAllBlockedDomains() {
        return blockedDomainRepository.findAll();
    }
    
    public boolean isDomainBlocked(String domain) {
        return blockedDomainRepository.exists(domain);
    }
}
