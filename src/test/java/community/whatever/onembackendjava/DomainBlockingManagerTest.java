package community.whatever.onembackendjava;

import community.whatever.onembackendjava.service.BlockedDomainService;
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
class DomainBlockingManagerTest {

    @Mock
    private BlockedDomainService blockedDomainService;

    @Nested
    @DisplayName("도메인 차단 기능 테스트")
    class DomainBlockingTest {
        
        @Test
        @DisplayName("도메인을 차단할 수 있다")
        void blockDomain_Success() {
            // given
            DomainBlockingManager manager = new DomainBlockingManager(blockedDomainService);
            String domain = "blocked-example.com";
            
            when(blockedDomainService.isDomainBlocked(domain)).thenReturn(true);
            
            // when
            manager.blockDomain(domain);
            boolean isBlocked = manager.isUrlBlocked("https://" + domain);
            
            // then
            assertTrue(isBlocked);
            verify(blockedDomainService).blockDomain(domain);
            verify(blockedDomainService).isDomainBlocked(domain);
        }
        
        @Test
        @DisplayName("www 접두사가 있는 도메인도 차단할 수 있다")
        void blockDomain_WithWwwPrefix_Success() {
            // given
            DomainBlockingManager manager = new DomainBlockingManager(blockedDomainService);
            String domain = "blocked-example.com";
            String domainWithWww = "www." + domain;
            
            when(blockedDomainService.isDomainBlocked(domain)).thenReturn(true);
            
            // when
            manager.blockDomain(domainWithWww);
            boolean isBlocked = manager.isUrlBlocked("https://" + domainWithWww);
            
            // then
            assertTrue(isBlocked);
            verify(blockedDomainService).blockDomain(domain);
            verify(blockedDomainService).isDomainBlocked(domain);
        }
        
        @Test
        @DisplayName("차단된 도메인을 해제할 수 있다")
        void unblockDomain_Success() {
            // given
            DomainBlockingManager manager = new DomainBlockingManager(blockedDomainService);
            String domain = "example.com";
            
            when(blockedDomainService.unblockDomain(domain)).thenReturn(true);
            when(blockedDomainService.isDomainBlocked(domain)).thenReturn(false);
            
            // when
            boolean result = manager.unblockDomain(domain);
            boolean isBlocked = manager.isUrlBlocked("https://" + domain);
            
            // then
            assertTrue(result);
            assertFalse(isBlocked);
            verify(blockedDomainService).unblockDomain(domain);
            verify(blockedDomainService).isDomainBlocked(domain);
        }
        
        @Test
        @DisplayName("차단되지 않은 도메인 해제 시도는 false를 반환한다")
        void unblockDomain_NonBlockedDomain_ReturnsFalse() {
            // given
            DomainBlockingManager manager = new DomainBlockingManager(blockedDomainService);
            String domain = "non-blocked-example.com";
            
            when(blockedDomainService.unblockDomain(domain)).thenReturn(false);
            
            // when
            boolean result = manager.unblockDomain(domain);
            
            // then
            assertFalse(result);
            verify(blockedDomainService).unblockDomain(domain);
        }
        
        @Test
        @DisplayName("모든 차단된 도메인 목록을 조회할 수 있다")
        void getBlockedDomains_ReturnsAllBlockedDomains() {
            // given
            DomainBlockingManager manager = new DomainBlockingManager(blockedDomainService);
            String domain1 = "example1.com";
            String domain2 = "example2.com";
            
            Set<String> blockedDomains = new HashSet<>();
            blockedDomains.add(domain1);
            blockedDomains.add(domain2);
            
            when(blockedDomainService.getAllBlockedDomains()).thenReturn(blockedDomains);
            
            // when
            Set<String> result = manager.getBlockedDomains();
            
            // then
            assertEquals(2, result.size());
            assertTrue(result.contains(domain1));
            assertTrue(result.contains(domain2));
            verify(blockedDomainService).getAllBlockedDomains();
        }
    }
}
