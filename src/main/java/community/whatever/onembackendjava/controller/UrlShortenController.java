package community.whatever.onembackendjava.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import community.whatever.onembackendjava.dto.req.ShortenedURLCreateRequest;
import community.whatever.onembackendjava.dto.res.ShortenedURLCreateResponse;
import community.whatever.onembackendjava.service.ShortenURLService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UrlShortenController {

	private final ShortenURLService service;

	@Deprecated
	@PostMapping("/shorten-url/search")
	public String shortenUrlSearch(@RequestBody String key) {
		String originURL = service.getOriginURL(key);
		return originURL;

	}

	@Deprecated
	@PostMapping("/shorten-url/create")
	public String shortenUrlCreate(@RequestBody String originUrl) {
		String key = service.createShortenedURL(originUrl);
		return key;
	}

	@GetMapping("/shorten-url")
	public ResponseEntity<String> getOriginalUrl(@RequestParam String shortenedURL) {
		String originURL = service.getOriginURL(shortenedURL);
		return ResponseEntity.ok(originURL);
	}

	@PostMapping("/shorten-url")
	public ResponseEntity<ShortenedURLCreateResponse> createShortenedURL(@RequestBody ShortenedURLCreateRequest req) {
		String originUrl = req.originURL();
		String shortenedURL = service.createShortenedURL(originUrl);
		ShortenedURLCreateResponse res = new ShortenedURLCreateResponse(shortenedURL);
		return ResponseEntity.ok(res);
	}

}
