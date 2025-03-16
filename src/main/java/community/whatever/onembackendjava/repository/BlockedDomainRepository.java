package community.whatever.onembackendjava.repository;

public interface BlockedDomainRepository {

	/**
	 * @param domain 도메인
	 * @return 도메인이 블랙리스트에 존재하는지
	 */
	boolean existsByBlockedDomainSuffix(String domain);

	String save(String domain);

	long count();

	void clear();
}
