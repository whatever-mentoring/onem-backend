package community.whatever.onembackendjava.controller.customValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * http, https 프로토콜만 허용하는 커스텀 Url 검증 어노테이션
 * <p>
 * 필드가 올바른 Url 형식인지 검증
 * (http, https)만 허용
 * </p>
 */
@Constraint(validatedBy = UrlValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUrl {

	String message() default "올바른 Url 형식이 아닙니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
