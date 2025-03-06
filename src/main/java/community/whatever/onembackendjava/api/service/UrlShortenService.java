package community.whatever.onembackendjava.api.service;

import community.whatever.onembackendjava.common.error.BusinessException;
import community.whatever.onembackendjava.common.error.BusinessExceptionGenerator;
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
            throw BusinessExceptionGenerator.createBusinessException("DB001");
        }
        return shortenUrls.get(key);
    }

    public String shortenUrlCreate(String originUrl){
        //@todo url validation 필요
        if (!StringUtils.hasText(originUrl)) {
            throw BusinessExceptionGenerator.createBusinessException("DB001");
        }
        Random random = new Random();
        return Stream.generate(() -> String.valueOf(random.nextInt(10000000)))
                .filter(key -> shortenUrls.putIfAbsent(key, originUrl) == null)
                .findFirst()
                //@Todo 키가 없을 경우 에러 생성되게 수정 필요
                .orElseThrow();
    }
}
