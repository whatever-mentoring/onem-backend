package community.whatever.onembackendjava.shortenurl.service;

import community.whatever.onembackendjava.common.exception.ErrorCode;
import community.whatever.onembackendjava.common.exception.custom.ExpiredUrlException;
import community.whatever.onembackendjava.common.exception.custom.NotFoundException;
import community.whatever.onembackendjava.shortenurl.component.ShortenUrlValidator;
import community.whatever.onembackendjava.shortenurl.component.ShortenUrlKeyGenerator;
import community.whatever.onembackendjava.shortenurl.dto.ShortenUrlResponse;
import community.whatever.onembackendjava.shortenurl.entity.ShortenUrl;
import community.whatever.onembackendjava.shortenurl.properties.ShortenUrlProperties;
import community.whatever.onembackendjava.shortenurl.repository.ShortenUrlRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class ShortenUrlService {

    private final ShortenUrlProperties shortenUrlProperties;
    private final ShortenUrlValidator shortenUrlValidator;
    private final ShortenUrlKeyGenerator shortenUrlKeyGenerator;
    private final ShortenUrlRepository shortenUrlRepository;

    public ShortenUrlService(ShortenUrlProperties shortenUrlProperties,
        ShortenUrlValidator shortenUrlValidator, ShortenUrlKeyGenerator shortenUrlKeyGenerator,
        ShortenUrlRepository shortenUrlRepository) {
        this.shortenUrlProperties = shortenUrlProperties;
        this.shortenUrlValidator = shortenUrlValidator;
        this.shortenUrlKeyGenerator = shortenUrlKeyGenerator;
        this.shortenUrlRepository = shortenUrlRepository;
    }

    /**
     * <p>단축 URL 생성</p>
     *
     * @param originUrl originUrl
     * @return 단축 URL 정보
     */
    public ShortenUrlResponse createShortenUrl(String originUrl) {
        shortenUrlValidator.validate(originUrl);

        ShortenUrl shortenUrl = ShortenUrl.create(
            originUrl,
            shortenUrlKeyGenerator.generate(),
            getExpirationTime(shortenUrlProperties.getExpiredDuration())
        );

        shortenUrlRepository.save(shortenUrl);
        return ShortenUrlResponse.from(shortenUrl);
    }

    private LocalDateTime getExpirationTime(Duration duration) {
        return LocalDateTime.now().plus(duration);
    }

    /**
     * <p>단축 URL 조회</p>
     *
     * @param shortenUrlKey 단축 URL KEY
     * @return 단축 URL 정보
     */
    public ShortenUrlResponse getOriginUrlByShortenUrlKey(String shortenUrlKey) {
        ShortenUrl shortenUrl = shortenUrlRepository.findByShortenUrlKey(shortenUrlKey)
            .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_SHORTEN_URL));

        validateNotExpired(shortenUrl);
        return ShortenUrlResponse.from(shortenUrl);
    }

    private void validateNotExpired(ShortenUrl shortenUrl) {
        if (shortenUrl.isExpired()) {
            throw new ExpiredUrlException(ErrorCode.EXPIRED_SHORTEN_URL);
        }
    }


}
