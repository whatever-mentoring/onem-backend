package community.whatever.onembackendjava.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import community.whatever.onembackendjava.service.ShortenURLService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UrlShortenController {

	private final ShortenURLService service;

	@PostMapping("/shorten-url/search")
	public String shortenUrlSearch(@RequestBody String key) {
		String shortenURL = service.getOriginURL(key);
		return shortenURL;
	}

	@PostMapping("/shorten-url/create")
	public String shortenUrlCreate(@RequestBody String originUrl) {
		String shortenedURL = service.createShortenedURL(originUrl);
		return shortenedURL;
	}
}
