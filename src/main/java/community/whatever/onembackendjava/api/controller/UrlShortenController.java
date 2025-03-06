package community.whatever.onembackendjava.api.controller;

import community.whatever.onembackendjava.api.service.UrlShortenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@RestController
@RequiredArgsConstructor
public class UrlShortenController {
    private final UrlShortenService urlShortenService;

    @GetMapping("/shorten-url")
    public String shortenUrlSearch(@Valid String key) {
        log.info("search key >> {}", key);
        String result = urlShortenService.shortenUrlSearch(key);
        log.info("return originUrl >> {}", result);
        return result;
    }

    @PostMapping("/shorten-url")
    public String shortenUrlCreate( @RequestBody String originUrl) {
        log.info("create originUrl >> {}", originUrl);
        String result = urlShortenService.shortenUrlCreate(originUrl);
        log.info("return randomKey >> {}", result);
        return result;
    }
}
