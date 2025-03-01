package community.whatever.onembackendjava.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessExceptionCode {
	ORIGIN_URL_NOT_FOUND(HttpStatus.NOT_FOUND, "원본 URL이 존재하지 않습니다."),
	IS_NOT_VALID_URL(HttpStatus.BAD_REQUEST, "유효한 도메인 형식이 아닙니다"),
	IS_BLOCKED_DOMAIN(HttpStatus.BAD_REQUEST, "해당 도메인은 블랙리스트에 포함되어 있습니다.");

	private final HttpStatus status;
	private final String errorMessage;

}
