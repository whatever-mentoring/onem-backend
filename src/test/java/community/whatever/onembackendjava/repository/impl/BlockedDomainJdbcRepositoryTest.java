package community.whatever.onembackendjava.repository.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;

@Import(BlockedDomainJdbcRepository.class)
@DataJdbcTest
class BlockedDomainJdbcRepositoryTest {

	@Autowired
	private BlockedDomainJdbcRepository repository;

	@DisplayName("도메인 삽입 성공")
	@Test
	public void t1() throws Exception {
		String blockedDomain = "blocked.com";

		repository.save(blockedDomain);

		boolean blocked = repository.existsByBlockedDomainSuffix(blockedDomain);
		assertTrue(blocked);
	}

	@DisplayName("저장된 도메인의 수를 정확히 조회할 수 있다.")
	@Test
	void t2() throws Exception {
		Long n = 3L;
		for (long i = 0; i < n; i++) {
			repository.save("www.naver.com " + i);
		}

		Long cnt = repository.count();
		assertThat(cnt).isEqualTo(n);
	}

	@DisplayName("삭제 테스트")
	@Test
	void t3() throws Exception {
		Long n = 3L;
		for (long i = 0; i < n; i++) {
			repository.save("www.naver.com " + i);
		}
		repository.clear();
		assertThat(repository.count()).isZero();
	}

	@DisplayName("차단된 도메인이 현제 도매인과 같거나, 현재 도메인의 하위 도메인일 경우 existsByBlockedDomainSuffix는 True를 반환한다.")
	@CsvSource(value = {
		"blocked.com,blocked.com,true",
		"blocked.com,www.blocked.com,true",
		"blocked.com,ablocked.com,false",
		"blocked.com,locked.com,false"
	})
	@ParameterizedTest(name = "{index} ==> domain ''{0}'' contains ''{1}'' is ''{2}''")
	public void t5(String domain, String domainWillBeChecked, boolean result) throws Exception {
		repository.save(domain);
		assertThat(repository.existsByBlockedDomainSuffix(domainWillBeChecked)).isEqualTo(result);
	}

}