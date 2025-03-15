package community.whatever.onembackendjava.service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import community.whatever.onembackendjava.dto.req.ShortenedURLCreateRequest;
import community.whatever.onembackendjava.dto.res.ShortenedURLCreateResponse;
import community.whatever.onembackendjava.entity.ShortenedURLEntity;
import community.whatever.onembackendjava.exception.BusinessExceptionCode;
import community.whatever.onembackendjava.exception.BusinessLogicException;
import community.whatever.onembackendjava.repository.BlockedDomainRepository;
import community.whatever.onembackendjava.repository.ShortenedUrlRepository;
import community.whatever.onembackendjava.utils.URLUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShortenURLService {

	private final ShortenedUrlRepository repository;

	private final BlockedDomainRepository blockedDomainRepository;

	private final AtomicLong atomicLong = new AtomicLong(0);

	@Value("${spring.profiles.active}")
	private String envPrefix;

	/**
	 * <P> 단축 URL로 원본 URL 조회</P>
	 *
	 * @param shortenedURL 단축 URL
	 * @return 원본 URL
	 * @throws BusinessLogicException 원본 URL을 찾을 수 없는 경우
	 */
	@Transactional(readOnly = true)
	public String getOriginURL(String shortenedURL) {
		ShortenedURLEntity entity = repository.findByShortenedURL(shortenedURL)
			.filter((se) -> se.getExpiredAt().isAfter(LocalDateTime.now()))
			.orElseThrow(() -> BusinessLogicException.from(BusinessExceptionCode.ORIGIN_URL_NOT_FOUND));

		return entity.getOriginUrl();
	}

	/**
	 * <p> 원본 URL로 단축 URL 생성</p>
	 *
	 * @param req ShortenedURLCreateRequest
	 * @return 생성된 단축 URL
	 * @throws BusinessLogicException 해당 URL 도메인이 블랙리스트에 있을 경우
	 */
	@Transactional
	public ShortenedURLCreateResponse createShortenedURL(ShortenedURLCreateRequest req) {
		if (checkDomainInBlackList(req.originURL())) {
			throw BusinessLogicException.withAdditionalInfo(BusinessExceptionCode.IS_BLOCKED_DOMAIN,
				"originURL: %s".formatted(req.originURL()));
		}
		String generatedShortenedURL = generateShortenedURL();

		LocalDateTime expiredAt = getExpirationTime(req.ttlMinutes());
		ShortenedURLEntity shortenedURLEntity = ShortenedURLEntity.of(req.originURL(), generatedShortenedURL,
			expiredAt);
		ShortenedURLEntity created = repository.save(shortenedURLEntity);
		
		return ShortenedURLCreateResponse.from(created);
	}

	/**
	 * <p> 단축 URL 생성</p>
	 *
	 * @return 단축 URL
	 */
	private String generateShortenedURL() {
		return envPrefix + "-" + atomicLong.incrementAndGet();
	}

	private boolean checkDomainInBlackList(String originURL) {
		String domain = URLUtils.extractDomainFromURL(originURL);
		return blockedDomainRepository.existsByBlockedDomainSuffix(domain);
	}

	private LocalDateTime getExpirationTime(int ttlMinutes) {
		return LocalDateTime.now().plusMinutes(ttlMinutes);
	}

}
