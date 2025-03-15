package community.whatever.onembackendjava.dto.res;

import java.time.LocalDateTime;

import community.whatever.onembackendjava.entity.ShortenedURLEntity;

public record ShortenedURLCreateResponse(
	Long id,
	String originURL,
	String shortenedURL,
	LocalDateTime expiredAt
) {
	public static ShortenedURLCreateResponse from(ShortenedURLEntity entity) {
		return new ShortenedURLCreateResponse(entity.getId(), entity.getOriginUrl(), entity.getShortenedUrl(),
			entity.getExpiredAt());
	}
}
