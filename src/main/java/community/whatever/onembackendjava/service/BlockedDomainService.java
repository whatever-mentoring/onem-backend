package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.entity.BlockedDomain;
import community.whatever.onembackendjava.repository.BlockedDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockedDomainService {
    
    private final BlockedDomainRepository blockedDomainRepository;
    
    /**
     * 도메인을 차단 목록에 추가합니다.
     * 도메인은 정규화된 형태로 저장됩니다.
     *
     * @param domain 차단할 도메인
     */
    public void blockDomain(String domain) {
        String normalizedDomain = normalizeDomain(domain);
        BlockedDomain blockedDomain = BlockedDomain.builder()
                .domain(normalizedDomain)
                .build();
        blockedDomainRepository.save(blockedDomain);
    }
    
    /**
     * 도메인을 차단 목록에서 제거합니다.
     * 도메인은 정규화된 형태로 검색됩니다.
     *
     * @param domain 차단 해제할 도메인
     * @return 차단 해제 성공 여부
     */
    public boolean unblockDomain(String domain) {
        String normalizedDomain = normalizeDomain(domain);
        return blockedDomainRepository.delete(normalizedDomain);
    }
    
    /**
     * 모든 차단된 도메인 목록을 조회합니다.
     *
     * @return 차단된 도메인 목록
     */
    public Set<String> getAllBlockedDomains() {
        return blockedDomainRepository.findAllDomains().stream()
                .map(BlockedDomain::getDomain)
                .collect(Collectors.toSet());
    }
    
    /**
     * 주어진 도메인이 차단되었는지 확인합니다.
     * 도메인은 정규화된 형태로 검색됩니다.
     *
     * @param domain 확인할 도메인
     * @return 차단 여부
     */
    public boolean isDomainBlocked(String domain) {
        String normalizedDomain = normalizeDomain(domain);
        return blockedDomainRepository.exists(normalizedDomain);
    }
    
    /**
     * URL이 차단된 도메인을 포함하는지 확인합니다.
     *
     * @param url 확인할 URL 문자열
     * @return 차단 여부
     */
    public boolean isUrlBlocked(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            if (host == null) {
                return false;
            }
            return isDomainBlocked(host);
        } catch (URISyntaxException e) {
            return false;
        }
    }
    
    /**
     * 도메인 문자열을 정규화합니다.
     * - 소문자로 변환
     * - www. 접두사 제거
     *
     * @param domain 정규화할 도메인 문자열
     * @return 정규화된 도메인 문자열
     */
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
