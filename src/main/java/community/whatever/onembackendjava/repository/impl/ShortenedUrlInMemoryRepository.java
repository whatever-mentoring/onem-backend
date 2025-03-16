package community.whatever.onembackendjava.repository.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import community.whatever.onembackendjava.entity.ShortenedUrlEntity;
import community.whatever.onembackendjava.repository.ShortenedUrlRepository;

@Repository
public class ShortenedUrlInMemoryRepository implements ShortenedUrlRepository {

	private final Map<String, ShortenedUrlEntity> shortenUrls = new ConcurrentHashMap<>();

	@Override
	public Optional<ShortenedUrlEntity> findByShortenedUrl(String shortenedUrl) {
		return Optional.ofNullable(shortenUrls.get(shortenedUrl));
	}

	@Override
	public ShortenedUrlEntity save(ShortenedUrlEntity shortenedUrlEntity) {
		shortenUrls.put(shortenedUrlEntity.getShortenedUrl(), shortenedUrlEntity);

		return shortenedUrlEntity;
	}
}
