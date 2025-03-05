package community.whatever.onembackendjava.utils;

import static lombok.AccessLevel.*;

import java.net.MalformedURLException;
import java.net.URL;

import community.whatever.onembackendjava.exception.BusinessExceptionCode;
import community.whatever.onembackendjava.exception.BusinessLogicException;
import lombok.NoArgsConstructor;

/**
 * URL 관련 로직 정리하는 클래스
 */
@NoArgsConstructor(access = PRIVATE)
public class URLUtils {

	public static String extractDomainFromURL(String url) {
		try {
			URL u = new URL(url);
			return u.getHost();
		} catch (MalformedURLException e) {
			throw BusinessLogicException.from(BusinessExceptionCode.IS_NOT_VALID_URL);
		}
	}
}
