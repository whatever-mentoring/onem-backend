package community.whatever.onembackendjava.entity;

import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortenedUrlEntity {

	private Long id;

	private String originUrl;

	private String shortenedUrl;

	private LocalDateTime expiredAt;

	private boolean disabled;

	@Builder(access = PRIVATE)
	private ShortenedUrlEntity(String originUrl, String shortenedUrl, LocalDateTime expiredAt) {
		this.originUrl = originUrl;
		this.shortenedUrl = shortenedUrl;
		this.expiredAt = expiredAt;
	}

	public static ShortenedUrlEntity of(String originUrl, String shortenedUrl, LocalDateTime expiredAt) {
		return ShortenedUrlEntity.builder()
			.originUrl(originUrl)
			.shortenedUrl(shortenedUrl)
			.expiredAt(expiredAt)
			.build();
	}

	public void disable() {
		this.disabled = true;
	}
}
