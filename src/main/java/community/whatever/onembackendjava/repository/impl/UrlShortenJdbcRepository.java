package community.whatever.onembackendjava.repository.impl;

import java.util.Map;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import community.whatever.onembackendjava.entity.ShortenedURLEntity;
import community.whatever.onembackendjava.repository.URLShortenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Repository
@Slf4j
public class UrlShortenJdbcRepository implements URLShortenRepository {

	private final NamedParameterJdbcTemplate template;

	private final RowMapper<ShortenedURLEntity> rowMapper = BeanPropertyRowMapper.newInstance(ShortenedURLEntity.class);

	@Override
	public Optional<ShortenedURLEntity> findByShortenedURL(String shortenedURL) {
		String sql = """
			SELECT id, origin_url, shortened_url, expired_at,disabled
			FROM SHORTENED_URL
			WHERE shortened_url = :shortenedURL
			""";
		Map<String, Object> param = Map.of("shortenedURL", shortenedURL);

		try {
			ShortenedURLEntity entity = template.queryForObject(sql, param, rowMapper);
			return Optional.of(entity);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	@Override
	public ShortenedURLEntity save(ShortenedURLEntity shortenedURLEntity) {
		String sql = """
			INSERT INTO SHORTENED_URL(origin_url,shortened_url,expired_at,disabled)
			VALUES (:originUrl, :shortenedUrl, :expiredAt, :disabled)
			""";

		BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(shortenedURLEntity);
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

		template.update(sql, param, keyHolder);
		shortenedURLEntity.setId(keyHolder.getKey().longValue());
		return shortenedURLEntity;
	}

}
