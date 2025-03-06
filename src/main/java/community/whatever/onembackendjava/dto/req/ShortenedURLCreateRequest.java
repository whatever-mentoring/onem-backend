package community.whatever.onembackendjava.dto.req;

import community.whatever.onembackendjava.controller.customValidator.ValidURL;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ShortenedURLCreateRequest(
	@ValidURL
	String originURL,

	@Min(value = 0, message = "만료 기간은 음수일 수 없습니다.")
	@Max(value = 1440, message = "만료 기간은 최대 하루(1440분) 입니다.")
	int ttlMinutes

) {
}
