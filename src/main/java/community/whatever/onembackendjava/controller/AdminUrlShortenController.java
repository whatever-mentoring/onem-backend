package community.whatever.onembackendjava.controller;

import community.whatever.onembackendjava.service.AdminUrlShortenService;
import community.whatever.onembackendjava.dto.BlockDomainRequest;
import community.whatever.onembackendjava.dto.BlockedDomainsResponse;
import community.whatever.onembackendjava.dto.ShortenUrlsMapResponse;
import community.whatever.onembackendjava.dto.BulkAddShortenUrlsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin URL Shortener", description = "URL 단축 관리자 API")
public class AdminUrlShortenController {

    private final AdminUrlShortenService adminUrlShortenService;

    @Operation(summary = "모든 단축 URL 조회", description = "등록된 모든 단축 URL 매핑을 조회합니다.")
    @GetMapping("/admin/shorten-urls")
    public ResponseEntity<ShortenUrlsMapResponse> getAllShortenUrls() {
        return ResponseEntity.ok(adminUrlShortenService.getAllShortenUrls());
    }

    @Operation(summary = "단축 URL 일괄 등록", description = "여러 단축 URL을 일괄 등록합니다.")
    @PostMapping("/admin/shorten-urls")
    public ResponseEntity<String> bulkAddShortenUrls(@RequestBody BulkAddShortenUrlsRequest request) {
        return ResponseEntity.ok(adminUrlShortenService.bulkAddShortenUrls(request));
    }
    
    @Operation(summary = "차단된 도메인 조회", description = "현재 차단된 도메인 목록을 조회합니다.")
    @GetMapping("/admin/blocked-domains")
    public ResponseEntity<BlockedDomainsResponse> getBlockedDomains() {
        return ResponseEntity.ok(adminUrlShortenService.getBlockedDomains());
    }
    
    @Operation(summary = "도메인 차단", description = "특정 도메인을 차단 목록에 추가합니다.")
    @PostMapping("/admin/block-domain")
    public ResponseEntity<String> blockDomain(@RequestBody BlockDomainRequest request) {
        return ResponseEntity.ok(adminUrlShortenService.blockDomain(request));
    }
    
    @Operation(summary = "도메인 차단 해제", description = "특정 도메인을 차단 목록에서 제거합니다.")
    @PostMapping("/admin/unblock-domain")
    public ResponseEntity<String> unblockDomain(@RequestBody BlockDomainRequest request) {
        return ResponseEntity.ok(adminUrlShortenService.unblockDomain(request));
    }
}
