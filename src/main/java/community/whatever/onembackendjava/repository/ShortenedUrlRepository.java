package community.whatever.onembackendjava.repository;

import java.util.Optional;

import community.whatever.onembackendjava.entity.ShortenedURLEntity;

public interface ShortenedUrlRepository {

	Optional<ShortenedURLEntity> findById(Long id);

	/**
	 * <p> 단축된 URL로 조회</p>
	 *
	 * @param shortenedURL 단축된 URL
	 * @return 단축 URL entity
	 */
	Optional<ShortenedURLEntity> findByShortenedURL(String shortenedURL);

	/**
	 * <p>원본 URL과 단축 URL을 저장</p>
	 *
	 * @param shortenedURLEntity 저장할 단축 URL 객체
	 * @return 저장된 단축 URL entity
	 */
	ShortenedURLEntity save(ShortenedURLEntity shortenedURLEntity);

	long count();

}
