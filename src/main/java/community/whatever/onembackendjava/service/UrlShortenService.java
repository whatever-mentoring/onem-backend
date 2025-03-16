package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.DomainBlockingManager;
import community.whatever.onembackendjava.constant.AppEnvironment;
import community.whatever.onembackendjava.constant.UrlConstants;
import community.whatever.onembackendjava.repository.ShortenUrlRepository;
import community.whatever.onembackendjava.domain.RandomKeyGenerator;
import community.whatever.onembackendjava.dto.*;
import community.whatever.onembackendjava.entity.ShortenUrl;
import community.whatever.onembackendjava.exception.UrlShortenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class UrlShortenService {
    private final DomainBlockingManager urlMappingManager;
    private final AppEnvironment appEnvironment;
    private final RandomKeyGenerator randomKeyGenerator;
    private final ShortenUrlRepository shortenUrlRepository;

    private static final long ONE_HOUR = 60 * 60 * 1000;

    public UrlShortenService(DomainBlockingManager urlMappingManager, AppEnvironment appEnvironment, ShortenUrlRepository shortenUrlDao) {
        this.urlMappingManager = urlMappingManager;
        this.appEnvironment = appEnvironment;
        this.randomKeyGenerator = new RandomKeyGenerator(appEnvironment.getPrefix());
        this.shortenUrlRepository = shortenUrlDao;
    }


    public SearchShortenUrlResponse searchShortenUrl(SearchShortenUrlRequest request) {
        validatePrefix(request.key());

        Optional<ShortenUrl> shortenUrlOpt = shortenUrlRepository.findByShortKey(request.key());
        if (shortenUrlOpt.isEmpty()) {
            throw UrlShortenException.notFound(request.key());
        }

        ShortenUrl shortenUrl = shortenUrlOpt.get();
        if (Instant.now().isAfter(shortenUrl.getExpiryTime())) {
            throw UrlShortenException.invalidUrl("%s is expired".formatted(request.key()));
        }

        return new SearchShortenUrlResponse(shortenUrl.getOriginalUrl());
    }

    public CreateShortenUrlResponse createShortenUrl(CreateShortenUrlRequest request) {
        String originUrl = request.originUrl();
        if (!originUrl.startsWith("http://") && !originUrl.startsWith("https://")) {
            originUrl = "https://" + originUrl;
        }

        validateUrl(originUrl);

        URI uri = getUri(originUrl);

        String host = uri.getHost();
        if (host != null && urlMappingManager.isUrlBlocked(host)) {
            throw UrlShortenException.blockedDomain(host);
        }

        Long ttlMinutes = request.ttlMinutes() != null ? request.ttlMinutes() : UrlConstants.DEFAULT_TTL_MINUTES;

        String randomKey;
        boolean success;
        do {
            randomKey = generateRandomKey();

            Optional<ShortenUrl> existingUrl = shortenUrlRepository.findByShortKey(randomKey);
            if (existingUrl.isEmpty()) {
                ShortenUrl shortenUrl = ShortenUrl.builder()
                        .shortKey(randomKey)
                        .originalUrl(originUrl)
                        .createdAt(Instant.now())
                        .expiryTime(Instant.now().plusSeconds(ttlMinutes * 60))
                        .build();

                shortenUrlRepository.save(shortenUrl);
                success = true;
            } else {
                success = false;
            }

        } while (!success);

        return new CreateShortenUrlResponse(randomKey);
    }

    private URI getUri(String originUrl) {
        URI uri;
        try {
            uri = new URI(originUrl);
        } catch (URISyntaxException e) {
            throw UrlShortenException.invalidUrl(e.getMessage());
        }
        return uri;
    }

    private void validateUrl(String url) {
        URI uri = getUri(url);
        String scheme = uri.getScheme();
        if (scheme == null) {
            throw UrlShortenException.invalidUrl(UrlConstants.URL_MUST_HAVE_SCHEME);
        }

        if (!scheme.equals("https") && !scheme.equals("http")) {
            throw UrlShortenException.invalidUrl(UrlConstants.ONLY_HTTP_HTTPS_ALLOWED);
        }

        String host = uri.getHost();
        if (host == null || host.isEmpty()) {
            throw UrlShortenException.invalidUrl(UrlConstants.URL_MUST_HAVE_VALID_HOST);
        }
    }

    /**
     * 랜덤 키를 생성합니다.
     * 키 생성 로직은 RandomKeyGenerator 도메인 객체로 분리되었습니다.
     * 이를 통해 단일 책임 원칙을 준수하고 테스트 용이성이 향상됩니다.
     *
     * @return 생성된 랜덤 키
     */
    private String generateRandomKey() {
        return randomKeyGenerator.generate();
    }

    public String getOriginalUrl(String code) {
        validatePrefix(code);

        // DAO를 통해 단축 URL 조회
        Optional<ShortenUrl> shortenUrlOpt = shortenUrlRepository.findByShortKey(code);
        if (shortenUrlOpt.isPresent()) {
            ShortenUrl shortenUrl = shortenUrlOpt.get();

            // 만료 시간 체크
            if (Instant.now().isAfter(shortenUrl.getExpiryTime())) {
                shortenUrlRepository.deleteByShortKey(code);
                throw UrlShortenException.notFound(code);
            }

            return shortenUrl.getOriginalUrl();
        } else {
            throw UrlShortenException.notFound(code);
        }
    }

    private void validatePrefix(String code) {
        if (code == null || code.length() < 3 || !code.startsWith(appEnvironment.getPrefix())) {
            throw UrlShortenException.invalidUrl("적합하지 않은 환경입니다 : " + appEnvironment.name());
        }
    }
}
