package community.whatever.onembackendjava.repository;

import community.whatever.onembackendjava.entity.BlockedDomain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BlockedDomainRepository Mock 테스트")
class BlockedDomainRepositoryMockTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private BlockedDomainRepositoryImpl blockedDomainRepository;

    @BeforeEach
    void setUp() {
        blockedDomainRepository = new BlockedDomainRepositoryImpl(jdbcTemplate);
    }

    @Test
    @DisplayName("도메인 저장 테스트")
    void save_shouldSaveBlockedDomain() {
        // given
        BlockedDomain domain = BlockedDomain.builder()
                .domainName("example.com")
                .build();
        
        // mock 설정
        doReturn(1).when(jdbcTemplate).update(any(), any(KeyHolder.class));
        
        // when
        boolean result = blockedDomainRepository.save(domain);
        
        // then
        assertThat(result).isTrue();
        verify(jdbcTemplate).update(any(), any(KeyHolder.class));
    }

    @Test
    @DisplayName("도메인 삭제 테스트")
    void delete_shouldRemoveBlockedDomain() {
        // given
        String domainName = "delete-test.com";
        doReturn(1).when(jdbcTemplate).update(anyString(), eq(domainName));
        
        // when
        boolean result = blockedDomainRepository.delete(domainName);
        
        // then
        assertThat(result).isTrue();
        verify(jdbcTemplate).update(anyString(), eq(domainName));
    }

    @Test
    @DisplayName("모든 도메인 조회 테스트")
    void findAllDomains_shouldReturnAllBlockedDomains() {
        // given
        List<BlockedDomain> expectedDomains = Arrays.asList(
                BlockedDomain.builder().id(1L).domainName("domain1.com").createdAt(Instant.now()).build(),
                BlockedDomain.builder().id(2L).domainName("domain2.com").createdAt(Instant.now()).build(),
                BlockedDomain.builder().id(3L).domainName("domain3.com").createdAt(Instant.now()).build()
        );
        
        // lenient 모드 사용 - 정확한 파라미터 매칭에 대한 엄격함 완화
        lenient().when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(expectedDomains);
        
        // when
        List<BlockedDomain> domains = blockedDomainRepository.findAllDomains();
        
        // then
        assertThat(domains).hasSize(3);
        assertThat(domains).extracting(BlockedDomain::getDomainName)
                .containsExactlyInAnyOrder("domain1.com", "domain2.com", "domain3.com");
    }

    @Test
    @DisplayName("도메인 존재 여부 확인 테스트")
    void exists_shouldReturnTrueForExistingDomain() {
        // given
        String existingDomain = "exists-test.com";
        String nonExistingDomain = "non-existing.com";
        
        doReturn(1).when(jdbcTemplate).queryForObject(anyString(), eq(Integer.class), eq(existingDomain));
        doReturn(0).when(jdbcTemplate).queryForObject(anyString(), eq(Integer.class), eq(nonExistingDomain));
        
        // when & then
        assertThat(blockedDomainRepository.exists(existingDomain)).isTrue();
        assertThat(blockedDomainRepository.exists(nonExistingDomain)).isFalse();
    }
}
