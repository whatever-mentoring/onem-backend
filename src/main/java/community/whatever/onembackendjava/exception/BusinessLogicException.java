package community.whatever.onembackendjava.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class BusinessLogicException extends RuntimeException {

	private final BusinessExceptionCode exceptionCode;
}
