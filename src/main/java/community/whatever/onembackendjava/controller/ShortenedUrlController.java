package community.whatever.onembackendjava.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import community.whatever.onembackendjava.dto.req.ShortenedUrlCreateRequest;
import community.whatever.onembackendjava.dto.res.OriginUrlResponse;
import community.whatever.onembackendjava.dto.res.ShortenedUrlCreateResponse;
import community.whatever.onembackendjava.service.ShortenedUrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ShortenedUrlController {

	private final ShortenedUrlService service;

	/**
	 * <p> 단축 Url을 통해 원본 Url 조회</p>
	 *
	 * @param shortenedUrl 단축 Url
	 * @return 원본 Url
	 */
	@GetMapping("/shorten-url/{shortenedUrl}")
	public OriginUrlResponse getOriginalUrl(@PathVariable String shortenedUrl) {
		String originUrl = service.getOriginUrl(shortenedUrl);
		return new OriginUrlResponse(originUrl);
	}

	/**
	 * <p> 단축 Url 생성 </p>
	 *
	 * @param req 원본 Url을 담고 있는 객체
	 * @return 단축 Url
	 */
	@PostMapping("/shorten-url")
	public ShortenedUrlCreateResponse createShortenedUrl(@RequestBody @Valid ShortenedUrlCreateRequest req) {
		return service.createShortenedUrl(req);
	}

}
