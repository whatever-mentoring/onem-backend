package community.whatever.onembackendjava.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

	@ExceptionHandler(BusinessLogicException.class)
	public ProblemDetail handleBusinessLogicException(BusinessLogicException e) {
		log.error("error =", e);
		var exceptionCode = e.getExceptionCode();
		return ProblemDetail.forStatusAndDetail(exceptionCode.getStatus(), exceptionCode.getDefaultErrorMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleMethodArgumentNotValidException(
		MethodArgumentNotValidException e) {

		ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		problemDetail.setTitle("필드 검증 오류");
		FieldError fieldError = e.getFieldError();
		problemDetail.setDetail(fieldError.getField() + " : " + fieldError.getDefaultMessage());

		return problemDetail;
	}
}
