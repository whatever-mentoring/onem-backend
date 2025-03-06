package community.whatever.onembackendjava.common.error;

import community.whatever.onembackendjava.common.error.model.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class BusinessExceptionGenerator {

    private static RestControllerAdvice restControllerAdvice;

    public BusinessExceptionGenerator(RestControllerAdvice restControllerAdvice){
        this.restControllerAdvice = restControllerAdvice;
    }

    public static BusinessException createBusinessException(String errorCode) {
        ErrorCode enumError = ErrorCode.valueOf(errorCode);
        return new BusinessException(enumError.getErrorCode(), enumError.getMessage());
    }

    public static BusinessException createBusinessException(String errorCode, String message, HttpStatus status) {
        ErrorCode enumError = ErrorCode.valueOf(errorCode);

        return new BusinessException(enumError.getErrorCode(), message, status);
    }

    public static BusinessException createBusinessException(String errorCode, String message) {
        ErrorCode enumError = ErrorCode.valueOf(errorCode);

        return new BusinessException(enumError.getErrorCode(), message, null);
    }
}
