package community.whatever.onembackendjava.dto;

import java.time.ZonedDateTime;
import java.time.ZoneId;

public record ErrorResponse(
    String errorCode,
    String message,
    ZonedDateTime timestamp
) {
    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse(errorCode, message, ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
    }
}
