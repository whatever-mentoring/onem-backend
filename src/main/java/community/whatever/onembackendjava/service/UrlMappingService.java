package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.dto.ShortenUrlWithExpiryInfo;
import community.whatever.onembackendjava.entity.ShortenUrl;
import community.whatever.onembackendjava.repository.ShortenUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UrlMappingService {
    
    private final ShortenUrlRepository shortenUrlRepository;
    
    public String findUrl(String key) {
        return shortenUrlRepository.findByShortKey(key)
                .filter(url -> !isExpired(url))
                .map(ShortenUrl::getOriginalUrl)
                .orElse(null);
    }
    
    public boolean saveUrl(String key, String url, Long ttlMinutes) {
        if (shortenUrlRepository.existsByShortKey(key)) {
            return false;
        }
        
        ShortenUrl shortenUrl = ShortenUrl.builder()
                .shortKey(key)
                .originalUrl(url)
                .expiryTime(Instant.now().plusSeconds(ttlMinutes * 60))
                .build();
        
        return shortenUrlRepository.save(shortenUrl);
    }
    
    private boolean isExpired(ShortenUrl url) {
        return Instant.now().isAfter(url.getExpiryTime());
    }
    
    public Map<String, String> findValidUrls() {
        return shortenUrlRepository.findAllValid().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getOriginalUrl()
                ));
    }
    
    public Map<String, ShortenUrlWithExpiryInfo> findAllUrls() {
        return shortenUrlRepository.findAll().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ShortenUrlWithExpiryInfo(
                                entry.getValue().getOriginalUrl(),
                                entry.getValue().getExpiryTime())
                ));
    }
    
    public void cleanExpiredUrls() {
        shortenUrlRepository.deleteExpired();
    }
}
