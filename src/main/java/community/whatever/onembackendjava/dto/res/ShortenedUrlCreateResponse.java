package community.whatever.onembackendjava.dto.res;

import java.time.LocalDateTime;

import community.whatever.onembackendjava.entity.ShortenedUrlEntity;

public record ShortenedUrlCreateResponse(
	Long id,
	String originUrl,
	String shortenedUrl,
	LocalDateTime expiredAt
) {
	public static ShortenedUrlCreateResponse from(ShortenedUrlEntity entity) {
		return new ShortenedUrlCreateResponse(entity.getId(), entity.getOriginUrl(), entity.getShortenedUrl(),
			entity.getExpiredAt());
	}
}
