package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.exception.InvalidShortenUriException;
import community.whatever.onembackendjava.repository.ShortenUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlShorteningService {

    private final ShortenUrlRepository shortenUrlRepository;

    public String searchOriginUri(String key) {
        try {
            return shortenUrlRepository.read(key);
        } catch (IllegalArgumentException e) {
            throw new InvalidShortenUriException(e.getMessage(), e);
        }
    }

    public String createShortenUri(String originUrl) {
        String randomKey = shortenUrlRepository.store(originUrl);
        return randomKey;
    }
}
