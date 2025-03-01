package community.whatever.onembackendjava.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import community.whatever.onembackendjava.exception.BusinessExceptionCode;
import community.whatever.onembackendjava.exception.BusinessLogicException;
import community.whatever.onembackendjava.repository.URLShortenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShortenURLService {

	private final URLShortenRepository repository;

	private final Random random = new Random();

	/**
	 * <P> 단축 URL로 원본 URL 조회</P>
	 *
	 * @param shortenedURL 단축 URL
	 * @return 원본 URL
	 * @throws BusinessLogicException 원본 URL을 찾을 수 없는 경우
	 */
	public String getOriginURL(String shortenedURL) {
		String originURL = repository.findByShortenedURL(shortenedURL)
			.orElseThrow(() -> new BusinessLogicException(BusinessExceptionCode.ORIGIN_URL_NOT_FOUND));

		return originURL;
	}

	/**
	 * <p> 원본 URL로 단축 URL 생성</p>
	 *
	 * @param originURL 원본 URL
	 * @return 생성된 단축 URL
	 */
	public String createShortenedURL(String originURL) {
		String generatedShortenedURL = generateShortenedURL();
		String shortenedURL = repository.create(originURL, generatedShortenedURL);

		return shortenedURL;
	}

	/**
	 * <p> 단축 URL 생성</p>
	 *
	 * @return 단축 URL
	 */
	private String generateShortenedURL() {
		return String.valueOf(random.nextInt());
	}

}
