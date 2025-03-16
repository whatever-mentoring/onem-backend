package community.whatever.onembackendjava.repository;

import java.util.Optional;

import community.whatever.onembackendjava.entity.ShortenedUrlEntity;

public interface ShortenedUrlRepository {

	Optional<ShortenedUrlEntity> findById(Long id);

	/**
	 * <p> 단축된 URL로 조회</p>
	 *
	 * @param shortenedUrl 단축된 URL
	 * @return 단축 URL entity
	 */
	Optional<ShortenedUrlEntity> findByShortenedURL(String shortenedUrl);

	/**
	 * <p>원본 URL과 단축 URL을 저장</p>
	 *
	 * @param shortenedURLEntity 저장할 단축 URL 객체
	 * @return 저장된 단축 URL entity
	 */
	ShortenedUrlEntity save(ShortenedUrlEntity shortenedURLEntity);

	long count();

	void clear();

}
