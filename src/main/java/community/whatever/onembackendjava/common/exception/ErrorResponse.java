package community.whatever.onembackendjava.common.exception;

public record ErrorResponse(
    String code,
    String message
) {
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(
            errorCode.name(),
            errorCode.getMessage()
        );
    }
}
