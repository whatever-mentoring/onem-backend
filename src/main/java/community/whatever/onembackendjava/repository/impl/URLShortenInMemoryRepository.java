package community.whatever.onembackendjava.repository.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import community.whatever.onembackendjava.entity.ShortenedURLEntity;
import community.whatever.onembackendjava.repository.URLShortenRepository;

@Repository
public class URLShortenInMemoryRepository implements URLShortenRepository {

	private final Map<String, ShortenedURLEntity> shortenUrls = new ConcurrentHashMap<>();

	@Override
	public Optional<ShortenedURLEntity> findByShortenedURL(String shortenedURL) {
		return Optional.ofNullable(shortenUrls.get(shortenedURL));
	}

	@Override
	public ShortenedURLEntity save(ShortenedURLEntity shortenedURLEntity) {
		shortenUrls.put(shortenedURLEntity.getShortenedURL(), shortenedURLEntity);

		return shortenedURLEntity;
	}
}
