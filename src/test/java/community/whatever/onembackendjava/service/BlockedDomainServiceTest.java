package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.entity.BlockedDomain;
import community.whatever.onembackendjava.repository.BlockedDomainRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
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
            String domainName = "blocked-example.com";
            String normalizedDomainName = domainName; // 이미 정규화된 도메인
            
            when(blockedDomainRepository.exists(normalizedDomainName)).thenReturn(true);
            when(blockedDomainRepository.save(any(BlockedDomain.class))).thenReturn(true);
            
            // when
            service.blockDomain(domainName);
            boolean isBlocked = service.isDomainBlocked(domainName);
            
            // then
            assertTrue(isBlocked);
            verify(blockedDomainRepository).save(any(BlockedDomain.class));
            verify(blockedDomainRepository).exists(normalizedDomainName);
        }
        
        @Test
        @DisplayName("www 접두사가 있는 도메인도 차단할 수 있다")
        void blockDomain_WithWwwPrefix_Success() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domainName = "blocked-example.com";
            String domainNameWithWww = "www." + domainName;
            
            when(blockedDomainRepository.exists(domainName)).thenReturn(true);
            when(blockedDomainRepository.save(any(BlockedDomain.class))).thenReturn(true);
            
            // when
            service.blockDomain(domainNameWithWww);
            boolean isBlocked = service.isDomainBlocked(domainNameWithWww);
            
            // then
            assertTrue(isBlocked);
            
            // 정규화된 도메인으로 저장되었는지 검증
            verify(blockedDomainRepository).save(argThat(blockedDomain -> 
                blockedDomain.getDomainName().equals(domainName)));
            verify(blockedDomainRepository).exists(domainName);
        }
        
        @Test
        @DisplayName("차단된 도메인을 해제할 수 있다")
        void unblockDomain_Success() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domainName = "example.com";
            
            when(blockedDomainRepository.delete(domainName)).thenReturn(true);
            when(blockedDomainRepository.exists(domainName)).thenReturn(false);
            
            // when
            boolean result = service.unblockDomain(domainName);
            boolean isBlocked = service.isDomainBlocked(domainName);
            
            // then
            assertTrue(result);
            assertFalse(isBlocked);
            verify(blockedDomainRepository).delete(domainName);
            verify(blockedDomainRepository).exists(domainName);
        }
        
        @Test
        @DisplayName("차단되지 않은 도메인 해제 시도는 false를 반환한다")
        void unblockDomain_NonBlockedDomain_ReturnsFalse() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domainName = "non-blocked-example.com";
            
            when(blockedDomainRepository.delete(domainName)).thenReturn(false);
            
            // when
            boolean result = service.unblockDomain(domainName);
            
            // then
            assertFalse(result);
            verify(blockedDomainRepository).delete(domainName);
        }
        
        @Test
        @DisplayName("모든 차단된 도메인 목록을 조회할 수 있다")
        void getAllBlockedDomains_ReturnsAllBlockedDomains() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domainName1 = "example1.com";
            String domainName2 = "example2.com";
            
            List<BlockedDomain> blockedDomainEntities = new ArrayList<>();
            blockedDomainEntities.add(BlockedDomain.builder().domainName(domainName1).build());
            blockedDomainEntities.add(BlockedDomain.builder().domainName(domainName2).build());
            
            when(blockedDomainRepository.findAllDomains()).thenReturn(blockedDomainEntities);
            
            // when
            Set<String> result = service.getAllBlockedDomains();
            
            // then
            assertEquals(2, result.size());
            assertTrue(result.contains(domainName1));
            assertTrue(result.contains(domainName2));
            verify(blockedDomainRepository).findAllDomains();
        }
        
        @Test
        @DisplayName("URL이 차단된 도메인을 포함하는지 확인할 수 있다")
        void isUrlBlocked_WithBlockedDomain_ReturnsTrue() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domainName = "blocked-example.com";
            String url = "https://www." + domainName + "/some/path";
            
            when(blockedDomainRepository.exists(domainName)).thenReturn(true);
            
            // when
            boolean result = service.isUrlBlocked(url);
            
            // then
            assertTrue(result);
            verify(blockedDomainRepository).exists(domainName);
        }
        
        @Test
        @DisplayName("URL이 차단되지 않은 도메인을 포함하는 경우 false를 반환한다")
        void isUrlBlocked_WithNonBlockedDomain_ReturnsFalse() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String domainName = "non-blocked-example.com";
            String url = "https://" + domainName + "/some/path";
            
            when(blockedDomainRepository.exists(domainName)).thenReturn(false);
            
            // when
            boolean result = service.isUrlBlocked(url);
            
            // then
            assertFalse(result);
            verify(blockedDomainRepository).exists(domainName);
        }
        
        @Test
        @DisplayName("잘못된 형식의 URL이 주어지면 false를 반환한다")
        void isUrlBlocked_WithInvalidUrl_ReturnsFalse() {
            // given
            BlockedDomainService service = new BlockedDomainService(blockedDomainRepository);
            String invalidUrl = "not-a-url"; // invalid://url에서 변경
            
            // when
            boolean result = service.isUrlBlocked(invalidUrl);
            
            // then
            assertFalse(result);
            verifyNoInteractions(blockedDomainRepository);
        }
    }
}
