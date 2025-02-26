package community.whatever.onembackendjava.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessExceptionCode {
	ORIGIN_URL_NOT_FOUND(HttpStatus.NOT_FOUND, "원본 URL이 존재하지 않습니다");

	private final HttpStatus status;
	private final String errorMessage;

}
