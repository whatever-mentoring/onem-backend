package community.whatever.onembackendjava.dao;

import community.whatever.onembackendjava.entity.BlockedDomain;

import java.util.List;

/**
 * 차단된 도메인에 대한 데이터 접근 인터페이스
 */
public interface BlockedDomainRepository {
    
    /**
     * 새로운 차단 도메인 정보를 저장합니다.
     *
     * @param blockedDomain 저장할 차단 도메인 엔티티
     * @return 저장 성공 여부
     */
    boolean save(BlockedDomain blockedDomain);
    
    /**
     * 도메인 차단 정보를 삭제합니다.
     *
     * @param domain 삭제할 도메인
     * @return 삭제 성공 여부
     */
    boolean delete(String domain);
    
    /**
     * 모든 차단된 도메인 정보를 조회합니다.
     *
     * @return 차단된 도메인 엔티티 목록
     */
    List<BlockedDomain> findAll();
    
    /**
     * 특정 도메인이 차단되었는지 확인합니다.
     *
     * @param domain 확인할 도메인
     * @return 차단 여부
     */
    boolean exists(String domain);
}
