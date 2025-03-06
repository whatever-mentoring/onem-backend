package community.whatever.onembackendjava.entity;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ShortenedURLEntity {

	private Long id;

	private String originURL;

	private String shortenedURL;

	private LocalDateTime expiredAt;

	private boolean isDeleted;

	@Builder
	public ShortenedURLEntity(Long id, String originURL, String shortenedURL, LocalDateTime expiredAt) {
		this.id = id;
		this.originURL = originURL;
		this.shortenedURL = shortenedURL;
		this.expiredAt = expiredAt;
	}

	public void markDelete() {
		this.isDeleted = true;
	}
}
