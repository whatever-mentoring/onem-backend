package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.UrlMappingManager;
import community.whatever.onembackendjava.constant.AdminConstants;
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
        return AdminConstants.BULK_ADD_SUCCESS;
    }
    
    public String blockDomain(BlockDomainRequest request) {
        urlMappingManager.blockDomain(request.domain());
        return AdminConstants.DOMAIN_BLOCK_SUCCESS;
    }
    
    public String unblockDomain(BlockDomainRequest request) {
        boolean removed = urlMappingManager.unblockDomain(request.domain());
        return removed ? AdminConstants.DOMAIN_UNBLOCK_SUCCESS : AdminConstants.DOMAIN_NOT_FOUND;
    }
    
    public BlockedDomainsResponse getBlockedDomains() {
        return new BlockedDomainsResponse(urlMappingManager.getBlockedDomains());
    }
}
