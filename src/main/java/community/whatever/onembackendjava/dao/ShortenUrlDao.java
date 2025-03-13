package community.whatever.onembackendjava.dao;

import community.whatever.onembackendjava.entity.ShortenUrl;

import java.util.List;
import java.util.Optional;

/**
 * 단축 URL에 대한 데이터 접근 인터페이스
 */
public interface ShortenUrlDao {
    
    /**
     * 새로운 단축 URL 정보를 저장합니다.
     *
     * @param shortenUrl 저장할 단축 URL 엔티티
     * @return 저장된 단축 URL 엔티티 (ID가 포함됨)
     */
    ShortenUrl save(ShortenUrl shortenUrl);
    
    /**
     * shortKey로 단축 URL 정보를 조회합니다.
     *
     * @param shortKey 조회할 단축 URL의 키
     * @return 조회된 단축 URL 엔티티 (Optional로 래핑됨)
     */
    Optional<ShortenUrl> findByShortKey(String shortKey);
    
    /**
     * 모든 단축 URL 정보를 조회합니다.
     *
     * @return 모든 단축 URL 엔티티 목록
     */
    List<ShortenUrl> findAll();
    
    /**
     * shortKey로 단축 URL 정보를 삭제합니다.
     *
     * @param shortKey 삭제할 단축 URL의 키
     * @return 삭제된 행의 수
     */
    int deleteByShortKey(String shortKey);
    
    /**
     * 만료된 단축 URL 정보를 조회합니다.
     *
     * @return 만료된 단축 URL 엔티티 목록
     */
    List<ShortenUrl> findExpired();
}
