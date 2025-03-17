package community.whatever.onembackendjava.repository.impl;

import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import community.whatever.onembackendjava.entity.ShortenedUrlEntity;
import community.whatever.onembackendjava.repository.ShortenedUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ShortenedUrlJdbcRepository implements ShortenedUrlRepository {

	private final JdbcClient jdbcClient;

	@Override
	public Optional<ShortenedUrlEntity> findById(Long id) {
		String sql = """
			SELECT id, origin_url, shortened_url, expired_at,disabled
			FROM SHORTENED_URL
			WHERE id = :id
			""";
		return jdbcClient.sql(sql)
			.param("id", 1)
			.query(ShortenedUrlEntity.class)
			.optional();
	}

	@Override
	public Optional<ShortenedUrlEntity> findByShortenedUrl(String shortenedUrl) {
		String sql = """
			SELECT id, origin_url, shortened_url, expired_at,disabled
			FROM SHORTENED_URL
			WHERE shortened_url = :shortenedUrl
			""";

		Optional<ShortenedUrlEntity> maybeEntity = jdbcClient.sql(sql)
			.param("shortenedUrl", shortenedUrl)
			.query(ShortenedUrlEntity.class)
			.optional();

		return maybeEntity;
	}

	@Override
	public ShortenedUrlEntity save(ShortenedUrlEntity shortenedUrlEntity) {
		String sql = """
			INSERT INTO SHORTENED_URL(origin_url, shortened_url, expired_at, disabled)
			VALUES (:originUrl, :shortenedUrl, :expiredAt, :disabled)
			""";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcClient.sql(sql)
			.paramSource(shortenedUrlEntity)
			.update(keyHolder);

		Long id = (Long)keyHolder.getKeys().get("ID");
		assert (id != null);

		shortenedUrlEntity.setId(id);
		return shortenedUrlEntity;
	}

	@Override
	public long count() {
		String sql = """
			SELECT count(*) from SHORTENED_URL;
			""";

		long cnt = jdbcClient.sql(sql)
			.query(long.class)
			.single();
		return cnt;
	}

	@Override
	public void clear() {
		jdbcClient.sql("DELETE FROM SHORTENED_URL")
			.update();
	}
}

