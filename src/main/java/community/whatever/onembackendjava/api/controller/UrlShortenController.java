package community.whatever.onembackendjava.api.controller;

import community.whatever.onembackendjava.api.dto.ShortenUrlDto;
import community.whatever.onembackendjava.api.service.UrlShortenService;
import community.whatever.onembackendjava.common.util.ResponseFormatter;
import community.whatever.onembackendjava.common.util.model.ResultJson;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/shorten-url")
public class UrlShortenController {
    private final UrlShortenService urlShortenService;

    @GetMapping
    public ResponseEntity<ResultJson> shortenUrlSearch(@Valid ShortenUrlDto.Get.Request param) {
        log.info("param >> {}", param.toString());
        ShortenUrlDto.Get.Response result = urlShortenService.shortenUrlSearch(param);
        log.info("result >> {}", result);
        return ResponseFormatter.ConvertResponse(result);
    }

    @PostMapping
    public ResponseEntity<ResultJson> shortenUrlCreate(@RequestBody ShortenUrlDto.Create.Request param) {
        log.info("param >> {}", param.toString());
        ShortenUrlDto.Create.Response result = urlShortenService.shortenUrlCreate(param);
        log.info("result >> {}", result);
        return ResponseFormatter.ConvertResponse(result);
    }
}
