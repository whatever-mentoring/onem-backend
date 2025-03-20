package community.whatever.onembackendjava.repository.command;

import community.whatever.onembackendjava.entity.BlockedDomain;

/**
 * 차단된 도메인에 대한 명령(Command) 작업을 수행하는 저장소 인터페이스
 * 쓰기 작업만 담당하며 주 데이터베이스에 연결됩니다.
 */
public interface BlockedDomainCommandRepository {
    
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
}
