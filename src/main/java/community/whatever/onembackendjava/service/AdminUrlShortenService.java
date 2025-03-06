package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.UrlMappingManager;
import community.whatever.onembackendjava.constant.AdminConstants;
import community.whatever.onembackendjava.dto.BlockDomainRequest;
import community.whatever.onembackendjava.dto.BlockedDomainsResponse;
import community.whatever.onembackendjava.dto.ShortenUrlsMapResponse;
import community.whatever.onembackendjava.dto.BulkAddShortenUrlsRequest;
import community.whatever.onembackendjava.dto.ShortenUrlWithExpiryInfo;
import community.whatever.onembackendjava.dto.ShortenUrlsWithExpiryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminUrlShortenService {
    
    private final UrlMappingManager urlMappingManager;


    public ShortenUrlsMapResponse getValidShortenUrls() {
        return new ShortenUrlsMapResponse(urlMappingManager.findValidUrls());
    }

    public ShortenUrlsWithExpiryResponse getAllShortenUrlsWithExpiry() {
        Map<String, ShortenUrlWithExpiryInfo> urlInfoMap = urlMappingManager.findAllUrls();
        return new ShortenUrlsWithExpiryResponse(urlInfoMap);
    }

    public String cleanExpiredUrls() {
        urlMappingManager.cleanExpiredUrls();
        return AdminConstants.CLEANUP_SUCCESS;
    }
    
    public String bulkAddShortenUrls(BulkAddShortenUrlsRequest request) {
        if (request.shortenUrls() != null) {
            request.shortenUrls().forEach(urlMappingManager::putIfAbsent);
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
