package community.whatever.onembackendjava.repository;

public interface BlockedDomainRepository {

	/**
	 * @param domain 도메인
	 * @return 블랙리스트 도메인의 서브도메인인지
	 */
	boolean existsByBlockedDomainSuffix(String domain);

	String save(String domain);

	long count();

	void clear();
}
