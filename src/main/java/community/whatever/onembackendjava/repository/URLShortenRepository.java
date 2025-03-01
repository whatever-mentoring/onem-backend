package community.whatever.onembackendjava.repository;

import java.util.Optional;

public interface URLShortenRepository {
	/**
	 * <p> 단축 URL로 저장되어 있는 원본 URL 조회</p>
	 *
	 * @param shortenedURL 단축 URL
	 * @return 원본 URL
	 */
	Optional<String> findByShortenedURL(String shortenedURL);

	/**
	 * <p>원본 URL과 단축 URL을 저장</p>
	 *
	 * @param originURL    저장할 원본 URL
	 * @param shortenedURL 저장할 단축 URL
	 * @return 저장된 단축 URL
	 */
	String create(String originURL, String shortenedURL);

}
