package community.whatever.onembackendjava.repository;

import community.whatever.onembackendjava.dao.ShortenUrlDao;
import community.whatever.onembackendjava.entity.ShortenUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ShortenUrlRepository {

    private final ShortenUrlDao shortenUrlDao;

    public boolean save(ShortenUrl shortenUrl) {
        try {
            ShortenUrl saved = shortenUrlDao.save(shortenUrl);
            shortenUrl.setId(saved.getId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Optional<ShortenUrl> findByShortKey(String shortKey) {
        return shortenUrlDao.findByShortKey(shortKey);
    }

    public Map<String, ShortenUrl> findAll() {
        List<ShortenUrl> urls = shortenUrlDao.findAll();
        Map<String, ShortenUrl> result = new HashMap<>();
        for (ShortenUrl url : urls) {
            result.put(url.getShortKey(), url);
        }
        return result;
    }

    public boolean deleteByShortKey(String shortKey) {
        return shortenUrlDao.deleteByShortKey(shortKey) > 0;
    }

    public List<ShortenUrl> findExpired() {
        return shortenUrlDao.findExpired();
    }
    
    public boolean existsByShortKey(String shortKey) {
        return shortenUrlDao.findByShortKey(shortKey).isPresent();
    }
    
    public Map<String, ShortenUrl> findAllValid() {
        List<ShortenUrl> allUrls = shortenUrlDao.findAll();
        Map<String, ShortenUrl> result = new HashMap<>();
        Instant now = Instant.now();
        
        for (ShortenUrl url : allUrls) {
            if (url.getExpiryTime().isAfter(now)) {
                result.put(url.getShortKey(), url);
            }
        }
        
        return result;
    }
    
    public void deleteExpired() {
        List<ShortenUrl> expiredUrls = shortenUrlDao.findExpired();
        for (ShortenUrl url : expiredUrls) {
            shortenUrlDao.deleteByShortKey(url.getShortKey());
        }
    }
}
