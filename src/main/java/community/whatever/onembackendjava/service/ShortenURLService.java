package community.whatever.onembackendjava.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import community.whatever.onembackendjava.repository.URLShortenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShortenURLService {

	private final URLShortenRepository repository;

	private final Random random = new Random();

	public String getOriginURL(String shortenedURL) {
		String originURL = repository.findByShortenedURL(shortenedURL)
			.orElseThrow(RuntimeException::new);

		return originURL;
	}

	public String createShortenedURL(String originURL) {
		String generatedShortenedURL = generateShortenedURL();
		String shortenedURL = repository.create(originURL, generatedShortenedURL);

		return generatedShortenedURL;
	}

	private String generateShortenedURL() {
		return String.valueOf(random.nextInt());
	}

}
