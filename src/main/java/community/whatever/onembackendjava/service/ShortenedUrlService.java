package community.whatever.onembackendjava.service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import community.whatever.onembackendjava.dto.req.ShortenedUrlCreateRequest;
import community.whatever.onembackendjava.dto.res.ShortenedUrlCreateResponse;
import community.whatever.onembackendjava.entity.ShortenedUrlEntity;
import community.whatever.onembackendjava.exception.BusinessExceptionCode;
import community.whatever.onembackendjava.exception.BusinessLogicException;
import community.whatever.onembackendjava.repository.BlockedDomainRepository;
import community.whatever.onembackendjava.repository.ShortenedUrlRepository;
import community.whatever.onembackendjava.utils.UrlUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShortenedUrlService {

	private final ShortenedUrlRepository repository;

	private final BlockedDomainRepository blockedDomainRepository;

	private final AtomicLong atomicLong = new AtomicLong(0);

	@Value("${spring.profiles.active}")
	private String envPrefix;

	/**
	 * <P> 단축 Url로 원본 Url 조회</P>
	 *
	 * @param shortenedUrl 단축 Url
	 * @return 원본 Url
	 * @throws BusinessLogicException 원본 Url을 찾을 수 없는 경우
	 */
	public String getOriginUrl(String shortenedUrl) {
		ShortenedUrlEntity entity = repository.findByShortenedUrl(shortenedUrl)
			.filter((se) -> se.getExpiredAt().isAfter(LocalDateTime.now()))
			.orElseThrow(() -> BusinessLogicException.from(BusinessExceptionCode.ORIGIN_URL_NOT_FOUND));

		return entity.getOriginUrl();
	}

	/**
	 * <p> 원본 Url로 단축 Url 생성</p>
	 *
	 * @param req ShortenedUrlCreateRequest
	 * @return 생성된 단축 Url
	 * @throws BusinessLogicException 해당 Url 도메인이 블랙리스트에 있을 경우
	 */
	public ShortenedUrlCreateResponse createShortenedUrl(ShortenedUrlCreateRequest req) {
		if (checkDomainInBlackList(req.originUrl())) {
			throw BusinessLogicException.withAdditionalInfo(BusinessExceptionCode.IS_BLOCKED_DOMAIN,
				"originUrl: %s".formatted(req.originUrl()));
		}
		String generatedShortenedUrl = generateShortenedUrl();

		LocalDateTime expiredAt = getExpirationTime(req.ttlMinutes());
		ShortenedUrlEntity shortenedUrlEntity = ShortenedUrlEntity.of(req.originUrl(), generatedShortenedUrl,
			expiredAt);
		ShortenedUrlEntity created = repository.save(shortenedUrlEntity);

		return ShortenedUrlCreateResponse.from(created);
	}

	/**
	 * <p> 단축 Url 생성</p>
	 *
	 * @return 단축 Url
	 */
	private String generateShortenedUrl() {
		return envPrefix + "-" + atomicLong.incrementAndGet();
	}

	private boolean checkDomainInBlackList(String originUrl) {
		String domain = UrlUtils.extractDomainFromUrl(originUrl);
		return blockedDomainRepository.exists(domain);
	}

	private LocalDateTime getExpirationTime(int ttlMinutes) {
		return LocalDateTime.now().plusMinutes(ttlMinutes);
	}

}
