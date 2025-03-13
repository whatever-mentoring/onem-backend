package community.whatever.onembackendjava.api.domain;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface BlockedDomainInterface {
    /**
     * CSV 파일에서 차단 목록 로드
     */
    void loadBlockedDomains(MultipartFile file);
    /**
     * 차단된 도메인 목록 반환
     */
    Set<String> getBlockedDomains();
    /**
     * 특정 URL이 차단된 도메인인지 확인
     */
    boolean isBlocked(String url);
}
