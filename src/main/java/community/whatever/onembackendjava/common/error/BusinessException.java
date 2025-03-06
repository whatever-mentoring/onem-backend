package community.whatever.onembackendjava.common.error;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    private String errorCode;
    private String message;
    private HttpStatus httpStatus;
    private HashMap<String, Object> data;

    public BusinessException() {
        super();
    }

    public BusinessException(String errorCode) {
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }


    public BusinessException( String message, HashMap<String, Object> data) {
        this.message = message;
        this.data = data;
    }

    public BusinessException(String errorCode, String message, HttpStatus httpStatus) {
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
