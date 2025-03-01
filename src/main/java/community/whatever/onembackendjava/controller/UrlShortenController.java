package community.whatever.onembackendjava.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import community.whatever.onembackendjava.dto.req.ShortenedURLCreateRequest;
import community.whatever.onembackendjava.dto.res.OriginURLResponse;
import community.whatever.onembackendjava.dto.res.ShortenedURLCreateResponse;
import community.whatever.onembackendjava.service.ShortenURLService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UrlShortenController {

	private final ShortenURLService service;

	/**
	 * @deprecated use {@link #getOriginalURL(String)}
	 */
	@Deprecated
	@PostMapping("/shorten-url/search")
	public String shortenUrlSearch(@RequestBody String key) {
		String shortenURL = service.getOriginURL(key);
		return shortenURL;
	}

	/**
	 * @deprecated use {@link #createShortenedURL(ShortenedURLCreateRequest)}
	 */
	@Deprecated
	@PostMapping("/shorten-url/create")
	public String shortenUrlCreate(@RequestBody String originUrl) {
		String shortenedURL = service.createShortenedURL(originUrl);
		return shortenedURL;
	}

	/**
	 * <p> 단축 URL을 통해 원본 URL 조회</p>
	 *
	 * @param shortenedURL 단축 URL
	 * @return 원본 URL
	 */
	@GetMapping("/shorten-url/{shortenedURL}")
	public OriginURLResponse getOriginalURL(@PathVariable String shortenedURL) {
		String originURL = service.getOriginURL(shortenedURL);
		return new OriginURLResponse(originURL);
	}

	/**
	 * <p> 단축 URL 생성 </p>
	 *
	 * @param req 원본 URL을 담고 있는 객체
	 * @return 단축 URL
	 */
	@PostMapping("/shorten-url")
	public ShortenedURLCreateResponse createShortenedURL(@RequestBody @Valid ShortenedURLCreateRequest req) {
		String originUrl = req.originURL();
		String shortenedURL = service.createShortenedURL(originUrl);
		ShortenedURLCreateResponse res = new ShortenedURLCreateResponse(shortenedURL);
		return res;
	}

}
