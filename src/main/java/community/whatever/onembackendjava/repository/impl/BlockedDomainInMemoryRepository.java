package community.whatever.onembackendjava.repository.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import community.whatever.onembackendjava.repository.BlockedDomainRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class BlockedDomainInMemoryRepository implements BlockedDomainRepository {

	private final List<String> blockedDomains = new ArrayList<>();

	@PostConstruct
	public void init() {

		try {
			ClassPathResource resource = new ClassPathResource("blockedDomainList.txt");
			InputStream inputStream = resource.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			int cnt = 0;
			while ((line = reader.readLine()) != null) {
				cnt++;
				String domain = line.trim().toLowerCase();
				checkIsValidDomain(domain);
				blockedDomains.add(generateReversedString(domain));
			}
			blockedDomains.sort(String::compareTo);
			log.debug("{}개의 블랙리스트 도메인이 등록되었습니다.", cnt);
		} catch (Exception e) {
			log.error("도메인 블랙리스트 초기화시 오류 발생 = ", e);
			throw new IllegalStateException(e);
		}
	}

	private void checkIsValidDomain(String maybeDomain) throws MalformedURLException {
		URL url = new URL("http://" + maybeDomain);
		if (!url.getHost().equals(maybeDomain)) {
			throw new IllegalStateException(maybeDomain + " 은 유효한 도메인이 아닙니다 확인 요망");
		}
	}

	private String generateReversedString(String domain) {
		return new StringBuilder(domain).reverse().toString();
	}

	@Override
	public boolean exists(String domain) {
		String reversedDomain = generateReversedString(domain);
		return blockedDomains.stream()  // HACK: 성능보고 bs?
			.anyMatch(blockedDomain -> reversedDomain.startsWith(blockedDomain));
	}
}
