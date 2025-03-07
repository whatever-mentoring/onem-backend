package community.whatever.onembackendjava;

import community.whatever.onembackendjava.constant.UrlConstants;
import community.whatever.onembackendjava.dto.ShortenUrlWithExpiryInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        @DisplayName("모든 단축 URL을 만료 정보와 함께 조회할 수 있다")
        void findAllUrls_ReturnsAllUrlsWithExpiryInfo() {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String key1 = "abc123";
            String key2 = "def456";
            String url1 = "https://example1.com";
            String url2 = "https://example2.com";
            
            manager.putIfAbsent(key1, url1);
            manager.putIfAbsent(key2, url2);
            
            // when
            Map<String, ShortenUrlWithExpiryInfo> urlsWithExpiry = manager.findAllUrls();
            
            // then
            assertEquals(2, urlsWithExpiry.size());
            assertEquals(url1, urlsWithExpiry.get(key1).originalUrl());
            assertEquals(url2, urlsWithExpiry.get(key2).originalUrl());
            assertNotNull(urlsWithExpiry.get(key1).expiryTime());
            assertNotNull(urlsWithExpiry.get(key2).expiryTime());
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
    
    @Nested
    @DisplayName("URL 만료(TTL) 기능 테스트")
    class UrlExpirationTest {
        
        @Test
        @DisplayName("TTL을 지정하여 URL을 추가하고 만료 전에는 조회 가능하다")
        void putWithTTL_BeforeExpiration_Success() {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String key = "ttl123";
            String url = "https://example.com";
            Long ttlMinutes = 5L; // 5분 TTL
            
            // when
            boolean result = manager.putIfAbsent(key, url, ttlMinutes);
            String foundUrl = manager.find(key);
            
            // then
            assertTrue(result);
            assertEquals(url, foundUrl);
        }
        
        @Test
        @DisplayName("TTL이 만료된 URL은 null을 반환한다")
        void find_ExpiredUrl_ReturnsNull() throws InterruptedException {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String key = "ttl123";
            String url = "https://example.com";
            Long ttlMinutes = 0L; // 즉시 만료되도록 0분 설정 (내부적으로 처리됨)
            
            manager.putIfAbsent(key, url, ttlMinutes);
            
            // 약간의 대기 시간 (내부 처리 시간 고려)
            TimeUnit.MILLISECONDS.sleep(100);
            
            // when
            String foundUrl = manager.find(key);
            
            // then
            assertNull(foundUrl);
        }
        
        @Test
        @DisplayName("유효한 URL만 findValidUrls 결과에 포함된다")
        void findValidUrls_IncludesOnlyValidUrls() throws InterruptedException {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String expiredKey = "expired";
            String validKey = "valid";
            String url1 = "https://expired.com";
            String url2 = "https://valid.com";
            
            manager.putIfAbsent(expiredKey, url1, 0L); // 즉시 만료
            manager.putIfAbsent(validKey, url2, 5L); // 5분 유효
            
            // 약간의 대기 시간
            TimeUnit.MILLISECONDS.sleep(100);
            
            // when
            Map<String, String> validUrls = manager.findValidUrls();
            
            // then
            assertEquals(1, validUrls.size());
            assertNull(validUrls.get(expiredKey));
            assertEquals(url2, validUrls.get(validKey));
        }
        
        @Test
        @DisplayName("모든 URL은 만료 여부와 상관없이 findAllUrls 결과에 포함된다")
        void findAllUrls_IncludesAllUrls() throws InterruptedException {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String expiredKey = "expired";
            String validKey = "valid";
            String url1 = "https://expired.com";
            String url2 = "https://valid.com";
            
            manager.putIfAbsent(expiredKey, url1, 0L); // 즉시 만료
            manager.putIfAbsent(validKey, url2, 5L); // 5분 유효
            
            // 약간의 대기 시간
            TimeUnit.MILLISECONDS.sleep(100);
            
            // when
            Map<String, ShortenUrlWithExpiryInfo> allUrls = manager.findAllUrls();
            
            // then
            assertEquals(2, allUrls.size());
            assertNotNull(allUrls.get(expiredKey));
            assertNotNull(allUrls.get(validKey));
            assertEquals(url1, allUrls.get(expiredKey).originalUrl());
            assertEquals(url2, allUrls.get(validKey).originalUrl());
            assertTrue(allUrls.get(expiredKey).expired());
            assertFalse(allUrls.get(validKey).expired());
        }
        
        @Test
        @DisplayName("cleanExpiredUrls 메서드는 만료된 URL을 제거한다")
        void cleanExpiredUrls_RemovesExpiredUrls() throws InterruptedException {
            // given
            UrlMappingManager manager = new UrlMappingManager();
            String expiredKey = "expired";
            String validKey = "valid";
            String url1 = "https://expired.com";
            String url2 = "https://valid.com";
            
            manager.putIfAbsent(expiredKey, url1, 0L); // 즉시 만료
            manager.putIfAbsent(validKey, url2, 5L); // 5분 유효
            
            // 약간의 대기 시간
            TimeUnit.MILLISECONDS.sleep(100);
            
            // when
            manager.cleanExpiredUrls();
            Map<String, ShortenUrlWithExpiryInfo> allUrls = manager.findAllUrls();
            
            // then
            assertEquals(1, allUrls.size());
            assertNull(manager.find(expiredKey));
            assertEquals(url2, manager.find(validKey));
        }
    }
}
