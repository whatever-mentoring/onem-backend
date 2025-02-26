package community.whatever.onembackendjava.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateShortenUrlRequest(
        @NotBlank(message = "URL이 비어있으면 안됩니다.")
        @Size(max = 2048, message = "URL은 2048자를 넘을 수 없습니다.")
        String originUrl
) {
}
