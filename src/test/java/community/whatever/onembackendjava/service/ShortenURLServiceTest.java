package community.whatever.onembackendjava.service;

import static org.assertj.core.api.Assertions.*;

import java.net.URL;
import java.time.LocalDateTime;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import net.datafaker.Faker;

import community.whatever.onembackendjava.dto.req.ShortenedURLCreateRequest;
import community.whatever.onembackendjava.dto.res.ShortenedURLCreateResponse;
import community.whatever.onembackendjava.entity.ShortenedURLEntity;
import community.whatever.onembackendjava.exception.BusinessExceptionCode;
import community.whatever.onembackendjava.exception.BusinessLogicException;
import community.whatever.onembackendjava.repository.ShortenedUrlRepository;
import community.whatever.onembackendjava.repository.impl.BlockedDomainJdbcRepository;

@SpringBootTest
@Transactional
class ShortenURLServiceTest {

	private final Faker faker = new Faker();

	@Autowired
	private ShortenedUrlRepository shortenedUrlRepository;

	@Autowired
	private ShortenURLService service;

	@Autowired
	private BlockedDomainJdbcRepository blockedDomainJdbcRepository;

	@Autowired
	private ShortenURLService shortenURLService;

	@DisplayName("단축 Url 생성 성공")
	@Test
	void t1() throws Exception {
		String originUrl = faker.internet().url();
		int ttl = 1000;
		ShortenedURLCreateRequest request = new ShortenedURLCreateRequest(originUrl, ttl);

		ShortenedURLCreateResponse createdShortenedURL = service.createShortenedURL(request);

		assertThat(createdShortenedURL.originURL()).isEqualTo(originUrl);
		Long id = createdShortenedURL.id();
		ShortenedURLEntity find = shortenedUrlRepository.findById(id).get();
		assertThat(find).extracting(ShortenedURLEntity::getOriginUrl).isEqualTo(originUrl);

	}

	@Test
	@DisplayName("도메인이 차단되었을 경우 단축 Url을 생성하면 비즈니스 오류가 발생한다.")
	void t3() throws Exception {
		String originUrl = faker.internet().url();
		String blockedDomain = new URL(originUrl).getHost();

		blockedDomainJdbcRepository.save(blockedDomain);

		assertThatThrownBy(() -> shortenURLService.createShortenedURL(new ShortenedURLCreateRequest(originUrl, 1000)))
			.isInstanceOf(BusinessLogicException.class)
			.asInstanceOf(InstanceOfAssertFactories.type(BusinessLogicException.class))
			.extracting(BusinessLogicException::getExceptionCode)
			.isEqualTo(BusinessExceptionCode.IS_BLOCKED_DOMAIN);
	}

	@DisplayName("단축 URL로 원본 Url을 조회할 수 있다.")
	@Test
	public void t4() throws Exception {
		String originUrl = faker.internet().url();
		String shortenedUrl = "aa";
		LocalDateTime localDateTime = LocalDateTime.now().plusDays(2);
		ShortenedURLEntity saved = shortenedUrlRepository.save(
			ShortenedURLEntity.of(originUrl, shortenedUrl, localDateTime));

		Long id = saved.getId();

		String findOriginUrl = shortenURLService.getOriginURL(shortenedUrl);
		assertThat(findOriginUrl).isEqualTo(originUrl);
	}

	@DisplayName("단축 URL 조회시 URL이 없으면 오류가 발생한다.")
	@Test
	public void t6() throws Exception {
		String shortenedUrl = "NOT-EXISTS";

		assertThatThrownBy(() -> service.getOriginURL(shortenedUrl))
			.isInstanceOf(BusinessLogicException.class)
			.asInstanceOf(InstanceOfAssertFactories.type(BusinessLogicException.class))
			.extracting(BusinessLogicException::getExceptionCode)
			.isEqualTo(BusinessExceptionCode.ORIGIN_URL_NOT_FOUND);
	}

	@DisplayName("단축 URL 조회시 만료시간이 지나면 오류가 발생한다.")
	@Test
	public void t7() throws Exception {
		String originUrl = faker.internet().url();
		String shortenedUrl = "aa";
		LocalDateTime localDateTime = LocalDateTime.now().minusSeconds(1);
		ShortenedURLEntity saved = shortenedUrlRepository.save(
			ShortenedURLEntity.of(originUrl, shortenedUrl, localDateTime));

		assertThatThrownBy(() -> service.getOriginURL(shortenedUrl))
			.isInstanceOf(BusinessLogicException.class)
			.asInstanceOf(InstanceOfAssertFactories.type(BusinessLogicException.class))
			.extracting(BusinessLogicException::getExceptionCode)
			.isEqualTo(BusinessExceptionCode.ORIGIN_URL_NOT_FOUND);
	}
}