package community.whatever.onembackendjava.entity;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortenedURLEntity {

	private Long id;

	private String originUrl;

	private String shortenedUrl;

	private LocalDateTime expiredAt;

	private boolean disabled;

	@Builder(access = PRIVATE)
	private ShortenedURLEntity(String originUrl, String shortenedUrl, LocalDateTime expiredAt) {
		this.originUrl = originUrl;
		this.shortenedUrl = shortenedUrl;
		this.expiredAt = expiredAt;
	}

	public static ShortenedURLEntity of(String originURL, String shortenedURL, LocalDateTime expiredAt) {
		return ShortenedURLEntity.builder()
			.originUrl(originURL)
			.shortenedUrl(shortenedURL)
			.expiredAt(expiredAt)
			.build();
	}

	public void disable() {
		this.disabled = true;
	}
}
