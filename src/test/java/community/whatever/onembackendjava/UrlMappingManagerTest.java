package community.whatever.onembackendjava;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UrlMappingManagerTest {

    @Nested
    @DisplayName("URL 관리 기능 테스트")
    class UrlManagementTest {
        
        @Test
        @DisplayName("새로운 URL을 추가하고 조회할 수 있다")
        void putAndFind_Success() {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String key = "abc123";
            String url = "https://example.com";
            
            // when
            boolean result = manager.putIfAbsent(key, url);
            String foundUrl = manager.find(key);
            
            // then
            assertTrue(result);
            assertEquals(url, foundUrl);
        }
        
        @Test
        @DisplayName("이미 존재하는 키에 URL을 추가할 수 없다")
        void putIfAbsent_ExistingKey_ReturnsFalse() {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String key = "abc123";
            String url1 = "https://example.com";
            String url2 = "https://another-example.com";
            
            // when
            boolean firstResult = manager.putIfAbsent(key, url1);
            boolean secondResult = manager.putIfAbsent(key, url2);
            String foundUrl = manager.find(key);
            
            // then
            assertTrue(firstResult);
            assertFalse(secondResult);
            assertEquals(url1, foundUrl);
        }
        
        @Test
        @DisplayName("존재하지 않는 키로 URL을 조회하면 null을 반환한다")
        void find_NonExistingKey_ReturnsNull() {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String key = "nonexistent";
            
            // when
            String foundUrl = manager.find(key);
            
            // then
            assertNull(foundUrl);
        }
        
        @Test
        @DisplayName("모든 단축 URL을 조회할 수 있다")
        void findAll_ReturnsAllUrls() {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String key1 = "abc123";
            String key2 = "def456";
            String url1 = "https://example1.com";
            String url2 = "https://example2.com";
            
            manager.putIfAbsent(key1, url1);
            manager.putIfAbsent(key2, url2);
            
            // when
            Map<String, String> allUrls = manager.findAll();
            
            // then
            assertEquals(2, allUrls.size());
            assertEquals(url1, allUrls.get(key1));
            assertEquals(url2, allUrls.get(key2));
        }
    }
    
    @Nested
    @DisplayName("도메인 차단 기능 테스트")
    class DomainBlockingTest {
        
        @Test
        @DisplayName("도메인을 차단할 수 있다")
        void blockDomain_Success() {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String domain = "blocked-example.com";
            String url = "https://" + domain;
            
            // when
            manager.blockDomain(domain);
            
            // then
            assertTrue(manager.isUrlBlocked(url));  // 전체 URL을 사용
        }
        
        @Test
        @DisplayName("www 접두사가 있는 도메인도 차단할 수 있다")
        void blockDomain_WithWwwPrefix_Success() {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String domain = "blocked-example.com";
            String domainWithWww = "www." + domain;
            String urlWithWww = "https://" + domainWithWww;
            
            // when
            manager.blockDomain(domain);
            
            // then
            assertTrue(manager.isUrlBlocked(urlWithWww));
        }
        
        @Test
        @DisplayName("차단된 도메인을 해제할 수 있다")
        void unblockDomain_Success() {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String domain = "example.com";
            String url = "https://" + domain;
            
            manager.blockDomain(domain);
            assertTrue(manager.isUrlBlocked(url));
            
            // when
            boolean result = manager.unblockDomain(domain);
            
            // then
            assertTrue(result);
            assertFalse(manager.isUrlBlocked(url));
        }
        
        @Test
        @DisplayName("차단되지 않은 도메인 해제 시도는 false를 반환한다")
        void unblockDomain_NonBlockedDomain_ReturnsFalse() {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String domain = "non-blocked-example.com";
            
            // when
            boolean result = manager.unblockDomain(domain);
            
            // then
            assertFalse(result);
        }
        
        @Test
        @DisplayName("모든 차단된 도메인 목록을 조회할 수 있다")
        void getBlockedDomains_ReturnsAllBlockedDomains() {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String domain1 = "example1.com";
            String domain2 = "example2.com";
            
            manager.blockDomain(domain1);
            manager.blockDomain(domain2);
            
            // when
            Set<String> blockedDomains = manager.getBlockedDomains();
            
            // then
            assertEquals(2, blockedDomains.size());
            assertTrue(blockedDomains.contains(domain1));
            assertTrue(blockedDomains.contains(domain2));
        }
    }
}
