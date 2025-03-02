package community.whatever.onembackendjava;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Slf4j
@RestController
public class UrlShortenController {

    private final Map<String, String> shortenUrls = new HashMap<>();

    @GetMapping("/shorten-url/search")
    public String shortenUrlSearch(@Valid String key) {
        log.info("search key >> {}", key);
        if (!shortenUrls.containsKey(key)) {
            throw new IllegalArgumentException("Invalid key");
        }
        String originUrl = shortenUrls.get(key);
        log.info("return originUrl >> {}", originUrl);
        return originUrl;
    }

    @PostMapping("/shorten-url/create")
    public String shortenUrlCreate( @RequestBody String originUrl) {
        log.info("create originUrl >> {}", originUrl);
        if (!StringUtils.hasText(originUrl)) {
            throw new IllegalArgumentException("Request URL No exists ");
        }
        String randomKey;
        while(true){
            randomKey = String.valueOf(new Random().nextInt(10000000));
            if (shortenUrls.putIfAbsent(randomKey, originUrl ) == null){
                break;
            }
        }
        log.info("return randomKey >> {}", randomKey);
        return randomKey;
    }
}
