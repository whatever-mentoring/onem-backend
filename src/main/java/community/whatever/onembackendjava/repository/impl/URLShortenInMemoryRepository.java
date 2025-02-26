package community.whatever.onembackendjava.repository.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import community.whatever.onembackendjava.repository.URLShortenRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class URLShortenInMemoryRepository implements URLShortenRepository {

	private final Map<String, String> shortenUrls = new HashMap<>();

	@Override
	public Optional<String> findByShortenedURL(String shortenedURL) {
		String originURL = shortenUrls.get(shortenedURL);
		return Optional.ofNullable(originURL);
	}

	@Override
	public String create(String originURL, String shortenURL) {
		shortenUrls.put(shortenURL, originURL);

		return shortenURL;
	}
}
