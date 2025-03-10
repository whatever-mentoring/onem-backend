package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.UrlMappingManager;
import community.whatever.onembackendjava.constant.AppEnvironment;
import community.whatever.onembackendjava.constant.UrlConstants;
import community.whatever.onembackendjava.domain.RandomKeyGenerator;
import community.whatever.onembackendjava.dto.*;
import community.whatever.onembackendjava.exception.UrlShortenException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;

@Service
public class UrlShortenService {
    private final UrlMappingManager urlMappingManager;
    private final AppEnvironment appEnvironment;
    private final RandomKeyGenerator randomKeyGenerator;
    
    private static final long ONE_HOUR = 60 * 60 * 1000;
    private final String envPrefix = appEnvironment.getPrefix();


    public UrlShortenService(UrlMappingManager urlMappingManager, AppEnvironment appEnvironment) {
        this.urlMappingManager = urlMappingManager;
        this.appEnvironment = appEnvironment;
        this.randomKeyGenerator = new RandomKeyGenerator(appEnvironment.getPrefix());
    }


    public SearchShortenUrlResponse searchShortenUrl(SearchShortenUrlRequest request) {
        validatePrefix(request.key());
        String url = urlMappingManager.find(request.key());
        if (url == null) {
            throw UrlShortenException.notFound(request.key());
        }
        return new SearchShortenUrlResponse(url);
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
            success = urlMappingManager.putIfAbsent(randomKey, originUrl, ttlMinutes);
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
        
        String url = urlMappingManager.find(code);
        if (url == null) {
            throw UrlShortenException.notFound(code);
        }
        return url;
    }

    private void validatePrefix(String code) {
        if (code == null || code.length() < 3 || !code.startsWith(appEnvironment.getPrefix())) {
            throw UrlShortenException.invalidUrl("적합하지 않은 환경입니다 : " + appEnvironment.name());
        }
    }
    
    @Scheduled(fixedRate = ONE_HOUR) 
    public void cleanupExpiredUrls() {
        urlMappingManager.cleanExpiredUrls();
    }
}
