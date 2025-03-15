package community.whatever.onembackendjava.dto.req;

import community.whatever.onembackendjava.controller.customValidator.ValidUrl;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ShortenedUrlCreateRequest(
	@ValidUrl
	String originUrl,

	@NotNull(message = "만료기간은 필수입니다")
	@Min(value = 0, message = "만료 기간은 음수일 수 없습니다.")
	@Max(value = 1440, message = "만료 기간은 최대 하루(1440분) 입니다.")
	Integer ttlMinutes

) {
}
