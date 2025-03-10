package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.UrlMappingManager;
import community.whatever.onembackendjava.constant.AppEnvironment;
import community.whatever.onembackendjava.constant.UrlConstants;
import community.whatever.onembackendjava.dto.CreateShortenUrlRequest;
import community.whatever.onembackendjava.dto.CreateShortenUrlResponse;
import community.whatever.onembackendjava.dto.SearchShortenUrlRequest;
import community.whatever.onembackendjava.dto.SearchShortenUrlResponse;
import community.whatever.onembackendjava.exception.UrlShortenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenServiceTest {

    @Mock
    private UrlMappingManager urlMappingManager;
    
    @Mock
    private AppEnvironment appEnvironment;

    private UrlShortenService urlShortenService;
    
    private final String TEST_PREFIX = "dev";
    
    @BeforeEach
    void setUp() {
        when(appEnvironment.getPrefix()).thenReturn(TEST_PREFIX);
        urlShortenService = new UrlShortenService(urlMappingManager, appEnvironment);
    }

    @Nested
    @DisplayName("URL 생성 테스트")
    class CreateShortenUrlTest {

        @Test
        @DisplayName("URL을 생성할 때 기본 TTL이 적용된다")
        void createShortenUrl_WithDefaultTTL_Success() {
            // given
            String originUrl = "https://example.com";
            CreateShortenUrlRequest request = new CreateShortenUrlRequest(originUrl, null);

            // URL 생성 성공 시뮬레이션
            when(urlMappingManager.putIfAbsent(anyString(), eq(originUrl), anyLong())).thenReturn(true);

            // when
            CreateShortenUrlResponse response = urlShortenService.createShortenUrl(request);

            // then
            assertNotNull(response);
            verify(urlMappingManager).putIfAbsent(anyString(), eq(originUrl), eq(UrlConstants.DEFAULT_TTL_MINUTES)); 
        }

        @Test
        @DisplayName("사용자가 지정한 TTL을 사용한다")
        void createShortenUrl_WithCustomTTL_Success() {
            // given
            String originUrl = "https://example.com";
            Long customTTL = 30L; // 30분
            CreateShortenUrlRequest request = new CreateShortenUrlRequest(originUrl, customTTL);

            // URL 생성 성공 시뮬레이션
            when(urlMappingManager.putIfAbsent(anyString(), eq(originUrl), anyLong())).thenReturn(true);

            // when
            CreateShortenUrlResponse response = urlShortenService.createShortenUrl(request);

            // then
            assertNotNull(response);
            verify(urlMappingManager).putIfAbsent(anyString(), eq(originUrl), eq(customTTL));
        }

        @Test
        @DisplayName("중복 키가 생성된 경우 재시도한다")
        void createShortenUrl_DuplicateKey_RetrySuccess() {
            // given
            String originUrl = "https://example.com";
            CreateShortenUrlRequest request = new CreateShortenUrlRequest(originUrl, 5L);

            // 첫 시도는 실패, 두 번째 시도는 성공 시뮬레이션
            when(urlMappingManager.putIfAbsent(anyString(), eq(originUrl), eq(5L)))
                    .thenReturn(false)
                    .thenReturn(true);

            // when
            CreateShortenUrlResponse response = urlShortenService.createShortenUrl(request);

            // then
            assertNotNull(response);
            verify(urlMappingManager, times(2)).putIfAbsent(anyString(), eq(originUrl), eq(5L));
        }
    }

    @Nested
    @DisplayName("URL 조회 테스트")
    class SearchShortenUrlTest {
        
        @Test
        @DisplayName("잘못된 프리픽스로 URL을 조회하면 예외가 발생한다")
        void searchShortenUrl_InvalidPrefix_ThrowsException() {
            String invalidKey = "invalid-key";
            SearchShortenUrlRequest request = new SearchShortenUrlRequest(invalidKey);
            when(appEnvironment.name()).thenReturn("DEV");
            

            assertThrows(UrlShortenException.class, () -> {
                urlShortenService.searchShortenUrl(request);
            });
            

            verify(appEnvironment, atLeastOnce()).getPrefix();
        }

        @Test
        @DisplayName("존재하는 URL을 조회할 수 있다")
        void searchShortenUrl_ExistingKey_Success() {
            // given
            String key = TEST_PREFIX + "-abc123";
            String url = "https://example.com";
            SearchShortenUrlRequest request = new SearchShortenUrlRequest(key);

            when(urlMappingManager.find(key)).thenReturn(url);

            // when
            SearchShortenUrlResponse response = urlShortenService.searchShortenUrl(request);

            // then
            assertEquals(url, response.originUrl());
        }

        @Test
        @DisplayName("만료된 URL은 조회할 수 없다")
        void searchShortenUrl_ExpiredUrl_ThrowsException() {
            // given
            String key = TEST_PREFIX + "-expired";
            SearchShortenUrlRequest request = new SearchShortenUrlRequest(key);

            // 만료된 URL은 null 반환
            when(urlMappingManager.find(key)).thenReturn(null);

            // when & then
            assertThrows(UrlShortenException.class, () -> {
                urlShortenService.searchShortenUrl(request);
            });
        }
    }

    @Nested
    @DisplayName("리다이렉션 테스트")
    class RedirectTest {
        
        @Test
        @DisplayName("잘못된 프리픽스로 URL을 조회하면 예외가 발생한다")
        void getOriginalUrl_InvalidPrefix_ThrowsException() {

            String invalidCode = "invalid-code";
            when(appEnvironment.name()).thenReturn("DEV");
            
            assertThrows(UrlShortenException.class, () -> {
                urlShortenService.getOriginalUrl(invalidCode);
            });
            
            verify(appEnvironment, atLeastOnce()).getPrefix();
        }

        @Test
        @DisplayName("유효한 코드로 원본 URL을 조회할 수 있다")
        void getOriginalUrl_ValidCode_Success() {
            // given
            String code = TEST_PREFIX + "-abc123";
            String url = "https://example.com";

            when(urlMappingManager.find(code)).thenReturn(url);

            // when
            String originalUrl = urlShortenService.getOriginalUrl(code);

            // then
            assertEquals(url, originalUrl);
        }

        @Test
        @DisplayName("만료된 코드로 원본 URL을 조회하면 예외가 발생한다")
        void getOriginalUrl_ExpiredCode_ThrowsException() {
            // given
            String code = TEST_PREFIX + "-expired";

            when(urlMappingManager.find(code)).thenReturn(null);

            // when & then
            assertThrows(UrlShortenException.class, () -> {
                urlShortenService.getOriginalUrl(code);
            });
        }
    }

    @Nested
    @DisplayName("스케줄링 테스트")
    class SchedulingTest {

        @Test
        @DisplayName("cleanupExpiredUrls 메서드는 UrlMappingManager를 호출한다")
        void cleanupExpiredUrls_CallsManager() {
            // when
            urlShortenService.cleanupExpiredUrls();

            // then
            verify(urlMappingManager).cleanExpiredUrls();
        }
    }
}
