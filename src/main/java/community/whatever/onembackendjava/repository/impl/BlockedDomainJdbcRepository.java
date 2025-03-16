package community.whatever.onembackendjava.repository.impl;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import community.whatever.onembackendjava.repository.BlockedDomainRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BlockedDomainJdbcRepository implements BlockedDomainRepository {

	private final JdbcClient jdbcClient;

	@Override
	public boolean existsByBlockedDomainSuffix(String domain) {
		String sql = """
			   SELECT EXISTS (
			         SELECT 1 FROM BLOCKED_DOMAIN
			         WHERE :domain LIKE CONCAT('%.', domain)
			     )
			""";

		Boolean exists = jdbcClient.sql(sql)
			.param("domain", '.' + domain)
			.query(boolean.class)
			.single();

		return exists;
	}

	@Override
	public String save(String domain) {
		String sql = """
			INSERT INTO BLOCKED_DOMAIN (domain) VALUES (:domain)
			""";
		jdbcClient.sql(sql)
			.param("domain", domain)
			.update();
		return domain;
	}

	@Override
	public long count() {
		String sql = """
			SELECT count(*) FROM BLOCKED_DOMAIN
			""";
		return jdbcClient.sql(sql)
			.query(long.class)
			.single();
	}

	@Override
	public void clear() {
		jdbcClient.sql("DELETE FROM BLOCKED_DOMAIN")
			.update();
	}
}
