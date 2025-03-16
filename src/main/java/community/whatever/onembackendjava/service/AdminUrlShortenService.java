package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.DomainBlockingManager;
import community.whatever.onembackendjava.constant.AdminConstants;
import community.whatever.onembackendjava.repository.ShortenUrlRepository;
import community.whatever.onembackendjava.dto.BlockDomainRequest;
import community.whatever.onembackendjava.dto.BlockedDomainsResponse;
import community.whatever.onembackendjava.dto.ShortenUrlsMapResponse;
import community.whatever.onembackendjava.dto.BulkAddShortenUrlsRequest;
import community.whatever.onembackendjava.dto.ShortenUrlWithExpiryInfo;
import community.whatever.onembackendjava.dto.ShortenUrlsWithExpiryResponse;
import community.whatever.onembackendjava.entity.ShortenUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUrlShortenService {

    private final DomainBlockingManager domainBlockingManager;
    private final ShortenUrlRepository shortenUrlRepository;

    public ShortenUrlsMapResponse getValidShortenUrls() {
        Map<String, String> validUrls = shortenUrlRepository.findAll().stream()
                .filter(url -> Instant.now().isBefore(url.getExpiryTime()))
                .collect(Collectors.toMap(
                        ShortenUrl::getShortKey,
                        ShortenUrl::getOriginalUrl,
                        (existing, replacement) -> existing,
                        HashMap::new
                ));

        return new ShortenUrlsMapResponse(validUrls);
    }

    public ShortenUrlsWithExpiryResponse getAllShortenUrlsWithExpiry() {
        Map<String, ShortenUrlWithExpiryInfo> urlInfoMap = shortenUrlRepository.findAll().stream()
                .collect(Collectors.toMap(
                        ShortenUrl::getShortKey,
                        url -> new ShortenUrlWithExpiryInfo(url.getOriginalUrl(), url.getExpiryTime()),
                        (existing, replacement) -> existing,
                        HashMap::new
                ));


        return new ShortenUrlsWithExpiryResponse(urlInfoMap);
    }


    public String bulkAddShortenUrls(BulkAddShortenUrlsRequest request) {
        if (request.shortenUrls() != null) {
            request.shortenUrls().forEach((key, url) -> {
                if (shortenUrlRepository.findByShortKey(key).isEmpty()) {
                    ShortenUrl shortenUrl = ShortenUrl.builder()
                            .shortKey(key)
                            .originalUrl(url)
                            .createdAt(Instant.now())
                            .expiryTime(Instant.now().plusSeconds(60 * 60 * 24 * 30)) // 30일 기본 유효기간
                            .build();

                    shortenUrlRepository.save(shortenUrl);
                }
            });
        }
        return AdminConstants.BULK_ADD_SUCCESS;
    }

    public String blockDomain(BlockDomainRequest request) {
        domainBlockingManager.blockDomain(request.domain());
        return AdminConstants.DOMAIN_BLOCK_SUCCESS;
    }

    public String unblockDomain(BlockDomainRequest request) {
        boolean removed = domainBlockingManager.unblockDomain(request.domain());
        return removed ? AdminConstants.DOMAIN_UNBLOCK_SUCCESS : AdminConstants.DOMAIN_NOT_FOUND;
    }

    public BlockedDomainsResponse getBlockedDomains() {
        return new BlockedDomainsResponse(domainBlockingManager.getBlockedDomains());
    }
}
