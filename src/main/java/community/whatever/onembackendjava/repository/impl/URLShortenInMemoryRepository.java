package community.whatever.onembackendjava.repository.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import community.whatever.onembackendjava.repository.URLShortenRepository;

@Repository
public class URLShortenInMemoryRepository implements URLShortenRepository {

	private final Map<String, String> shortenUrls = new ConcurrentHashMap<>();

	@Override
	public Optional<String> findByShortenedURL(String shortenedURL) {
		String originURL = shortenUrls.get(shortenedURL);
		return Optional.ofNullable(originURL);
	}

	@Override
	public String save(String originURL, String shortenedURL) {
		shortenUrls.put(shortenedURL, originURL);

		return shortenedURL;
	}
}
