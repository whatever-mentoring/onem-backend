package community.whatever.onembackendjava.repository.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;

import net.datafaker.Faker;

import community.whatever.onembackendjava.entity.ShortenedUrlEntity;
import community.whatever.onembackendjava.repository.ShortenedUrlRepository;

@Import(ShortenedUrlJdbcRepository.class)
@DataJdbcTest
class ShortenedUrlJdbcRepositoryTest {

	private final Faker faker = new Faker();

	@Autowired
	private ShortenedUrlRepository repository;

	@DisplayName("삽입, Id로 조회 성공 테스트")
	@Test
	public void t0() throws Exception {
		String originUrl = faker.internet().url();
		String shortenedUrl = "gg";
		LocalDateTime expiredAt = LocalDateTime.of(2222, 2, 2, 2, 2, 2).plusHours(2);
		ShortenedUrlEntity saved = repository.save(ShortenedUrlEntity.of(originUrl, shortenedUrl, expiredAt));

		ShortenedUrlEntity find = repository.findById(saved.getId()).get();

		// assertFalse(saved.equals(find) || saved == find);
		assertThat(find).extracting(ShortenedUrlEntity::getId, ShortenedUrlEntity::getShortenedUrl,
				ShortenedUrlEntity::getOriginUrl,
				ShortenedUrlEntity::getExpiredAt)
			.containsExactly(find.getId(), shortenedUrl, originUrl, expiredAt);
	}

	@DisplayName("삽입,조회 성공 테스트")
	@Test
	public void t1() throws Exception {
		String originUrl = faker.internet().url();
		String shortenedUrl = "gg";
		LocalDateTime expiredAt = LocalDateTime.of(2222, 2, 2, 2, 2, 2).plusHours(2);
		ShortenedUrlEntity created = ShortenedUrlEntity.of(originUrl, shortenedUrl, expiredAt);
		repository.save(created);

		ShortenedUrlEntity find = repository.findByShortenedURL(shortenedUrl).get();

		assertThat(find.getId()).isNotNull();
		assertThat(find).extracting(ShortenedUrlEntity::getShortenedUrl, ShortenedUrlEntity::getOriginUrl,
				ShortenedUrlEntity::getExpiredAt)
			.containsExactly(shortenedUrl, originUrl, expiredAt);

	}

	@DisplayName("조회 실패시 null을 반환한다.")
	@Test
	public void t2() throws Exception {
		Optional<ShortenedUrlEntity> res = repository.findByShortenedURL("NOT_EXISTS_SHORTENED_URL");
		assertTrue(res.isEmpty());
	}

	@DisplayName("삭제 테스트")
	@Test
	void t3() throws Exception {
		Long n = 3L;
		for (long i = 0; i < n; i++) {
			repository.save(ShortenedUrlEntity.of("www.google.com1", "1", LocalDateTime.now()));
		}
		repository.clear();
		assertThat(repository.count()).isZero();
	}

	@DisplayName("count는 정확한 개수를 가져온다")
	@TestFactory
	Stream<DynamicTest> t5() {
		return Stream.of(
			dynamicTest("데이터가없으면 0을 반환한다.", () -> {
				assertThat(repository.count()).isZero();
			}),
			dynamicTest("데이터를 하나 추가하면 1을 반환한다.", () -> {
				repository.save(ShortenedUrlEntity.of("www.google.com1", "1", LocalDateTime.now()));
				assertThat(repository.count()).isOne();
			}),
			dynamicTest("데이터를 하나 더 추가하면 2를 반환한다.", () -> {
				repository.save(ShortenedUrlEntity.of("www.google.com1", "1", LocalDateTime.now()));
				assertThat(repository.count()).isEqualTo(2L);
			})
		);
	}

}