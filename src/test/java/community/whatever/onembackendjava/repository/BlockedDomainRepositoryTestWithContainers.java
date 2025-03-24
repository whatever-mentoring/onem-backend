package community.whatever.onembackendjava.repository;

import community.whatever.onembackendjava.config.AbstractTestcontainersTest;
import community.whatever.onembackendjava.entity.BlockedDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class BlockedDomainRepositoryTestWithContainers extends AbstractTestcontainersTest {

    @Autowired
    private BlockedDomainRepository blockedDomainRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 테스트 실행 전 테이블 초기화
        jdbcTemplate.execute("DELETE FROM blocked_domains");
        
        // 테이블이 없는 경우 생성
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS blocked_domains (
                id SERIAL PRIMARY KEY,
                domain VARCHAR(255) NOT NULL UNIQUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);
    }

    @Test
    void save_shouldSaveBlockedDomain() {
        // given
        BlockedDomain domain = BlockedDomain.builder()
                .domainName("example.com")
                .build();

        // when
        boolean result = blockedDomainRepository.save(domain);

        // then
        assertThat(result).isTrue();
        assertThat(domain.getId()).isNotNull();
        assertThat(blockedDomainRepository.exists("example.com")).isTrue();
    }

    @Test
    void delete_shouldRemoveBlockedDomain() {
        // given
        BlockedDomain domain = BlockedDomain.builder()
                .domainName("delete-test.com")
                .build();
        blockedDomainRepository.save(domain);
        assertThat(blockedDomainRepository.exists("delete-test.com")).isTrue();

        // when
        boolean result = blockedDomainRepository.delete("delete-test.com");

        // then
        assertThat(result).isTrue();
        assertThat(blockedDomainRepository.exists("delete-test.com")).isFalse();
    }

    @Test
    void findAllDomains_shouldReturnAllBlockedDomains() {
        // given
        blockedDomainRepository.save(BlockedDomain.builder().domainName("domain1.com").build());
        blockedDomainRepository.save(BlockedDomain.builder().domainName("domain2.com").build());
        blockedDomainRepository.save(BlockedDomain.builder().domainName("domain3.com").build());

        // when
        List<BlockedDomain> domains = blockedDomainRepository.findAllDomains();

        // then
        assertThat(domains).hasSize(3);
        assertThat(domains).extracting(BlockedDomain::getDomainName)
                .containsExactlyInAnyOrder("domain1.com", "domain2.com", "domain3.com");
    }

    @Test
    void exists_shouldReturnTrueForExistingDomain() {
        // given
        blockedDomainRepository.save(BlockedDomain.builder().domainName("exists-test.com").build());

        // when & then
        assertThat(blockedDomainRepository.exists("exists-test.com")).isTrue();
        assertThat(blockedDomainRepository.exists("non-existing.com")).isFalse();
    }
}
