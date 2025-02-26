package community.whatever.onembackendjava;

import community.whatever.onembackendjava.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlShortenController {

    private final UrlShortenService urlShortenService;

    @PostMapping("/shorten-url/search")
    public SearchShortenUrlResponse shortenUrlSearch(@RequestBody SearchShortenUrlRequest request) {
        return urlShortenService.searchShortenUrl(request);
    }

    @PostMapping("/shorten-url/create")
    public CreateShortenUrlResponse shortenUrlCreate(@Valid @RequestBody CreateShortenUrlRequest request) {
        return urlShortenService.createShortenUrl(request);
    }

    @GetMapping("/shorten--urls")
    public HashMapResponse getAllShortenUrls() {
        return urlShortenService.getAllShortenUrls();
    }

    @PostMapping("/shorten--urls")
    public String postShortenUrls(@RequestBody PostShortenUrlsRequest request) {
        return urlShortenService.addToShortenUrls(request);
    }
}
