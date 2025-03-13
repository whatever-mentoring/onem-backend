package community.whatever.onembackendjava.common.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class BlackListService {
    private final List<String> blockedDomains;

    public BlackListService(Environment env) {
        String domains = env.getProperty("spring.blacklist.domains", ""); // 기본값 빈 문자열
        this.blockedDomains = Arrays.stream(domains.split(","))
                .filter(domain -> !domain.isEmpty())
                .toList();
    }

    public List<String> getBlockedDomains() {
        return blockedDomains;
    }
}
