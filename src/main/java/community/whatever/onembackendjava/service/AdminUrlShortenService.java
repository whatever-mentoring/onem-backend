package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.UrlMappingManager;
import community.whatever.onembackendjava.dto.BlockDomainRequest;
import community.whatever.onembackendjava.dto.BlockedDomainsResponse;
import community.whatever.onembackendjava.dto.ShortenUrlsMapResponse;
import community.whatever.onembackendjava.dto.BulkAddShortenUrlsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUrlShortenService {
    
    private final UrlMappingManager urlMappingManager;
    
    public ShortenUrlsMapResponse getAllShortenUrls() {
        return new ShortenUrlsMapResponse(urlMappingManager.findAll());
    }
    
    public String bulkAddShortenUrls(BulkAddShortenUrlsRequest request) {
        if (request.shortenUrls() != null) {
            request.shortenUrls().forEach((key, url) -> 
                urlMappingManager.putIfAbsent(key, url));
        }
        return "Success";
    }
    
    public String blockDomain(BlockDomainRequest request) {
        urlMappingManager.blockDomain(request.domain());
        return "도메인 block 성공";
    }
    
    public String unblockDomain(BlockDomainRequest request) {
        boolean removed = urlMappingManager.unblockDomain(request.domain());
        return removed ? "도메인 블록 해제 성공" : "해당 도메인은 발견되지 않았습니다.";
    }
    
    public BlockedDomainsResponse getBlockedDomains() {
        return new BlockedDomainsResponse(urlMappingManager.getBlockedDomains());
    }
}
