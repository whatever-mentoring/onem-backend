package community.whatever.onembackendjava.controller.customValidator;

import java.net.MalformedURLException;
import java.net.URL;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class URLValidator implements ConstraintValidator<ValidURL, String> {
	@Override
	public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
		if (value == null || value.isBlank()) {
			return false;
		}

		try {
			URL url = new URL(value);
			String protocol = url.getProtocol();

			return protocol.equals("http") || protocol.equals("https");
		} catch (MalformedURLException e) {
			return false;
		}
	}
}
