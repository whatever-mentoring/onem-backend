package community.whatever.onembackendjava.entity;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ShortenedURLEntity {

	private Long id;

	private String originURL;

	private String shortenedURL;

	private LocalDateTime expiredAt;

	private boolean disabled;

	@Builder(access = PRIVATE)
	private ShortenedURLEntity(String originURL, String shortenedURL, LocalDateTime expiredAt) {
		this.originURL = originURL;
		this.shortenedURL = shortenedURL;
		this.expiredAt = expiredAt;
	}

	public static ShortenedURLEntity of(String originURL, String shortenedURL, LocalDateTime expiredAt) {
		return ShortenedURLEntity.builder()
			.originURL(originURL)
			.shortenedURL(shortenedURL)
			.expiredAt(expiredAt)
			.build();
	}

	public void disable() {
		this.disabled = true;
	}
}
