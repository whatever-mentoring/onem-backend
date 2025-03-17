package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.entity.BlockedDomain;
import community.whatever.onembackendjava.repository.BlockedDomainRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockedDomainServiceTest {

    @Mock
    private BlockedDomainRepository blockedDomainRepository;

    @Nested
    @DisplayName("도메인 차단 기능 테스트")
    class DomainBlockingTest {
        
        @Test
        @DisplayName("도메인을 차단할 수 있다")
        void blockDomain_Success() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domain = "blocked-example.com";
            String normalizedDomain = domain; // 이미 정규화된 도메인
            
            when(blockedDomainRepository.exists(normalizedDomain)).thenReturn(true);
            when(blockedDomainRepository.save(any(BlockedDomain.class))).thenReturn(true);
            
            // when
            service.blockDomain(domain);
            boolean isBlocked = service.isDomainBlocked(domain);
            
            // then
            assertTrue(isBlocked);
            verify(blockedDomainRepository).save(any(BlockedDomain.class));
            verify(blockedDomainRepository).exists(normalizedDomain);
        }
        
        @Test
        @DisplayName("www 접두사가 있는 도메인도 차단할 수 있다")
        void blockDomain_WithWwwPrefix_Success() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domain = "blocked-example.com";
            String domainWithWww = "www." + domain;
            
            when(blockedDomainRepository.exists(domain)).thenReturn(true);
            when(blockedDomainRepository.save(any(BlockedDomain.class))).thenReturn(true);
            
            // when
            service.blockDomain(domainWithWww);
            boolean isBlocked = service.isDomainBlocked(domainWithWww);
            
            // then
            assertTrue(isBlocked);
            
            // 정규화된 도메인으로 저장되었는지 검증
            verify(blockedDomainRepository).save(argThat(blockedDomain -> 
                blockedDomain.getDomain().equals(domain)));
            verify(blockedDomainRepository).exists(domain);
        }
        
        @Test
        @DisplayName("차단된 도메인을 해제할 수 있다")
        void unblockDomain_Success() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domain = "example.com";
            
            when(blockedDomainRepository.delete(domain)).thenReturn(true);
            when(blockedDomainRepository.exists(domain)).thenReturn(false);
            
            // when
            boolean result = service.unblockDomain(domain);
            boolean isBlocked = service.isDomainBlocked(domain);
            
            // then
            assertTrue(result);
            assertFalse(isBlocked);
            verify(blockedDomainRepository).delete(domain);
            verify(blockedDomainRepository).exists(domain);
        }
        
        @Test
        @DisplayName("차단되지 않은 도메인 해제 시도는 false를 반환한다")
        void unblockDomain_NonBlockedDomain_ReturnsFalse() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domain = "non-blocked-example.com";
            
            when(blockedDomainRepository.delete(domain)).thenReturn(false);
            
            // when
            boolean result = service.unblockDomain(domain);
            
            // then
            assertFalse(result);
            verify(blockedDomainRepository).delete(domain);
        }
        
        @Test
        @DisplayName("모든 차단된 도메인 목록을 조회할 수 있다")
        void getAllBlockedDomains_ReturnsAllBlockedDomains() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domain1 = "example1.com";
            String domain2 = "example2.com";
            
            Set<String> blockedDomains = new HashSet<>();
            blockedDomains.add(domain1);
            blockedDomains.add(domain2);
            
            when(blockedDomainRepository.findAll()).thenReturn(blockedDomains);
            
            // when
            Set<String> result = service.getAllBlockedDomains();
            
            // then
            assertEquals(2, result.size());
            assertTrue(result.contains(domain1));
            assertTrue(result.contains(domain2));
            verify(blockedDomainRepository).findAll();
        }
        
        @Test
        @DisplayName("URL이 차단된 도메인을 포함하는지 확인할 수 있다")
        void isUrlBlocked_WithBlockedDomain_ReturnsTrue() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domain = "blocked-example.com";
            String url = "https://www." + domain + "/some/path";
            
            when(blockedDomainRepository.exists(domain)).thenReturn(true);
            
            // when
            boolean result = service.isUrlBlocked(url);
            
            // then
            assertTrue(result);
            verify(blockedDomainRepository).exists(domain);
        }
        
        @Test
        @DisplayName("URL이 차단되지 않은 도메인을 포함하는 경우 false를 반환한다")
        void isUrlBlocked_WithNonBlockedDomain_ReturnsFalse() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domain = "non-blocked-example.com";
            String url = "https://" + domain + "/some/path";
            
            when(blockedDomainRepository.exists(domain)).thenReturn(false);
            
            // when
            boolean result = service.isUrlBlocked(url);
            
            // then
            assertFalse(result);
            verify(blockedDomainRepository).exists(domain);
        }
        
        @Test
        @DisplayName("잘못된 형식의 URL이 주어지면 false를 반환한다")
        void isUrlBlocked_WithInvalidUrl_ReturnsFalse() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String invalidUrl = "invalid://url";
            
            // when
            boolean result = service.isUrlBlocked(invalidUrl);
            
            // then
            assertFalse(result);
            // 잘못된 URL이므로 repository는 호출되지 않아야 함
            verifyNoInteractions(blockedDomainRepository);
        }
    }
}
