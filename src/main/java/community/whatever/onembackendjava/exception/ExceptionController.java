package community.whatever.onembackendjava.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import community.whatever.onembackendjava.exception.res.BusinessExceptionRes;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

	@ExceptionHandler(BusinessLogicException.class)
	public ResponseEntity<BusinessExceptionRes> handleBusinessLogicException(BusinessLogicException e) {
		BusinessExceptionCode ec = e.getExceptionCode();
		BusinessExceptionRes res = new BusinessExceptionRes(ec.getErrorMessage());

		return ResponseEntity.status(ec.getStatus()).body(res);
	}
}
