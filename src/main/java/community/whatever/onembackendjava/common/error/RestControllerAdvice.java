package community.whatever.onembackendjava.common.error;

import community.whatever.onembackendjava.common.util.model.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@Slf4j
@ControllerAdvice
public class RestControllerAdvice {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<ResultJson> BdExceptionHandler(BusinessException ex) {

        return ResponseEntity
                .status((ex.getHttpStatus() !=null) ? ex.getHttpStatus() : HttpStatus.OK)
                .body(ResultJson.builder()
                        .resultCode(ex.getErrorCode())
                        .msg(ex.getMessage())
                        .data(ex.getData())
                        .build());
    }

}
