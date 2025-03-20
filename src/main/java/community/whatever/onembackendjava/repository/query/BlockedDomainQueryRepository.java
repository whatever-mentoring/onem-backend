package community.whatever.onembackendjava.repository.query;

import community.whatever.onembackendjava.entity.BlockedDomain;

import java.util.List;

/**
 * 차단된 도메인에 대한 쿼리(Query) 작업을 수행하는 저장소 인터페이스
 * 읽기 작업만 담당하며 레플리카 데이터베이스에 연결됩니다.
 */
public interface BlockedDomainQueryRepository {
    
    /**
     * 모든 차단된 도메인 엔티티를 조회합니다.
     *
     * @return 차단된 도메인 엔티티 목록
     */
    List<BlockedDomain> findAllDomains();
    
    /**
     * 특정 도메인이 차단되었는지 확인합니다.
     *
     * @param domain 확인할 도메인
     * @return 차단 여부
     */
    boolean exists(String domain);
}
