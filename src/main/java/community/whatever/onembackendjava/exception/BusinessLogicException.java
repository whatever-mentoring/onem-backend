package community.whatever.onembackendjava.exception;

import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException {

	private final BusinessExceptionCode exceptionCode;

	private BusinessLogicException(BusinessExceptionCode exceptionCode, String additionalInfo) {
		super(exceptionCode.getDefaultErrorMessage() + (additionalInfo == null ? "" :
			"| additionalInfo: " + additionalInfo));
		this.exceptionCode = exceptionCode;
	}

	public static BusinessLogicException from(BusinessExceptionCode exceptionCode) {
		return new BusinessLogicException(exceptionCode, null);
	}

	public static BusinessLogicException withAdditionalInfo(BusinessExceptionCode exceptionCode,
		String additionalInfo) {
		return new BusinessLogicException(exceptionCode, additionalInfo);
	}

}
