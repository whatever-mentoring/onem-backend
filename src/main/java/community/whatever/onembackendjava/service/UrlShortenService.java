package community.whatever.onembackendjava.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
@Service
public class UrlShortenService {

    private final Map<String, String> shortenUrls = new HashMap<>();

    public String shortenUrlSearch(String key){
        if (!shortenUrls.containsKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }
        return shortenUrls.get(key);
    }

    public String shortenUrlCreate(String originUrl){
        if (!StringUtils.hasText(originUrl)) {
            throw new IllegalArgumentException("Request URL No exists ");
        }
        Random random = new Random();
        return Stream.generate(() -> String.valueOf(random.nextInt(10000000)))
                .filter(key -> shortenUrls.putIfAbsent(key, originUrl) == null)
                .findFirst()
                .orElseThrow();
    }
}
