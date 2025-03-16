package community.whatever.onembackendjava.repository;

import java.util.Optional;

import community.whatever.onembackendjava.entity.ShortenedUrlEntity;

public interface ShortenedUrlRepository {
	/**
	 * <p> 단축된 Url로 조회</p>
	 *
	 * @param shortenedUrl 단축된 Url
	 * @return 단축 Url entity
	 */
	Optional<ShortenedUrlEntity> findByShortenedUrl(String shortenedUrl);

	/**
	 * <p>원본 Url과 단축 Url을 저장</p>
	 *
	 * @param shortenedUrlEntity 저장할 단축 Url 객체
	 * @return 저장된 단축 Url entity
	 */
	ShortenedUrlEntity save(ShortenedUrlEntity shortenedUrlEntity);

}
