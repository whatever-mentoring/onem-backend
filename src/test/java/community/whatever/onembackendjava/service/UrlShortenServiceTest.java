package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.DomainBlockingManager;
import community.whatever.onembackendjava.dao.ShortenUrlDao;
import community.whatever.onembackendjava.constant.AppEnvironment;
import community.whatever.onembackendjava.dto.CreateShortenUrlRequest;
import community.whatever.onembackendjava.dto.CreateShortenUrlResponse;
import community.whatever.onembackendjava.dto.SearchShortenUrlRequest;
import community.whatever.onembackendjava.dto.SearchShortenUrlResponse;
import community.whatever.onembackendjava.entity.ShortenUrl;
import community.whatever.onembackendjava.exception.UrlShortenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenServiceTest {

    @Mock
    private DomainBlockingManager urlMappingManager;
    
    @Mock
    private AppEnvironment appEnvironment;
    
    @Mock
    private ShortenUrlDao shortenUrlDao;

    private UrlShortenService urlShortenService;
    
    private final String TEST_PREFIX = "dev";
    
    @BeforeEach
    void setUp() {
        when(appEnvironment.getPrefix()).thenReturn(TEST_PREFIX);
        urlShortenService = new UrlShortenService(urlMappingManager, appEnvironment, shortenUrlDao);
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

            // DB에 URL이 존재하지 않음을 시뮬레이션
            when(shortenUrlDao.findByShortKey(anyString())).thenReturn(Optional.empty());
            
            // DAO를 통한 저장 성공 시뮬레이션
            when(shortenUrlDao.save(any(ShortenUrl.class))).thenReturn(new ShortenUrl());

            // when
            CreateShortenUrlResponse response = urlShortenService.createShortenUrl(request);

            // then
            assertNotNull(response);
            verify(shortenUrlDao).save(any(ShortenUrl.class));
        }

        @Test
        @DisplayName("사용자가 지정한 TTL을 사용한다")
        void createShortenUrl_WithCustomTTL_Success() {
            // given
            String originUrl = "https://example.com";
            Long customTTL = 30L; // 30분
            CreateShortenUrlRequest request = new CreateShortenUrlRequest(originUrl, customTTL);

            // DB에 URL이 존재하지 않음을 시뮬레이션
            when(shortenUrlDao.findByShortKey(anyString())).thenReturn(Optional.empty());
            
            // DAO를 통한 저장 성공 시뮬레이션
            when(shortenUrlDao.save(any(ShortenUrl.class))).thenReturn(new ShortenUrl());

            // when
            CreateShortenUrlResponse response = urlShortenService.createShortenUrl(request);

            // then
            assertNotNull(response);
            verify(shortenUrlDao).save(any(ShortenUrl.class));
        }

        @Test
        @DisplayName("중복 키가 생성된 경우 재시도한다")
        void createShortenUrl_DuplicateKey_RetrySuccess() {
            // given
            String originUrl = "https://example.com";
            CreateShortenUrlRequest request = new CreateShortenUrlRequest(originUrl, 5L);

            // 첫 번째 키는 이미 존재, 두 번째 키는 존재하지 않음을 시뮬레이션
            when(shortenUrlDao.findByShortKey(anyString()))
                    .thenReturn(Optional.of(new ShortenUrl()))
                    .thenReturn(Optional.empty());
            
            // URL 매핑 매니저를 통한 저장 시뮬레이션 (첫 번째 시도는 DAO에서 이미 존재함을 확인하고 매핑 매니저로 넘어감)
            when(urlMappingManager.putIfAbsent(anyString(), eq(originUrl), eq(5L)))
                    .thenReturn(true);
                    
            // DAO를 통한 저장 성공 시뮬레이션 (두 번째 시도에서 사용)
            when(shortenUrlDao.save(any(ShortenUrl.class))).thenReturn(new ShortenUrl());

            // when
            CreateShortenUrlResponse response = urlShortenService.createShortenUrl(request);

            // then
            assertNotNull(response);
            verify(shortenUrlDao, times(2)).findByShortKey(anyString());
            verify(urlMappingManager).putIfAbsent(anyString(), eq(originUrl), eq(5L));
            verify(shortenUrlDao).save(any(ShortenUrl.class));
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
        @DisplayName("존재하는 URL을 DB에서 조회할 수 있다")
        void searchShortenUrl_ExistingKeyInDb_Success() {
            // given
            String key = TEST_PREFIX + "-abc123";
            String url = "https://example.com";
            SearchShortenUrlRequest request = new SearchShortenUrlRequest(key);

            ShortenUrl shortenUrl = ShortenUrl.builder()
                    .shortKey(key)
                    .originalUrl(url)
                    .build();

            when(shortenUrlDao.findByShortKey(key)).thenReturn(Optional.of(shortenUrl));

            // when
            SearchShortenUrlResponse response = urlShortenService.searchShortenUrl(request);

            // then
            assertEquals(url, response.originUrl());
        }
        
        @Test
        @DisplayName("존재하는 URL을 매핑 매니저에서 조회할 수 있다")
        void searchShortenUrl_ExistingKeyInManager_Success() {
            // given
            String key = TEST_PREFIX + "-abc123";
            String url = "https://example.com";
            SearchShortenUrlRequest request = new SearchShortenUrlRequest(key);

            when(shortenUrlDao.findByShortKey(key)).thenReturn(Optional.empty());
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

            // DB에 URL이 존재하지 않음을 시뮬레이션
            when(shortenUrlDao.findByShortKey(key)).thenReturn(Optional.empty());
            
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
        @DisplayName("유효한 코드로 DB에서 원본 URL을 조회할 수 있다")
        void getOriginalUrl_ValidCodeInDb_Success() {
            // given
            String code = TEST_PREFIX + "-abc123";
            String url = "https://example.com";

            ShortenUrl shortenUrl = ShortenUrl.builder()
                    .shortKey(code)
                    .originalUrl(url)
                    .build();

            when(shortenUrlDao.findByShortKey(code)).thenReturn(Optional.of(shortenUrl));

            // when
            String originalUrl = urlShortenService.getOriginalUrl(code);

            // then
            assertEquals(url, originalUrl);
        }
        
        @Test
        @DisplayName("유효한 코드로 매핑 매니저에서 원본 URL을 조회할 수 있다")
        void getOriginalUrl_ValidCodeInManager_Success() {
            // given
            String code = TEST_PREFIX + "-abc123";
            String url = "https://example.com";

            when(shortenUrlDao.findByShortKey(code)).thenReturn(Optional.empty());
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

            when(shortenUrlDao.findByShortKey(code)).thenReturn(Optional.empty());
            when(urlMappingManager.find(code)).thenReturn(null);

            // when & then
            assertThrows(UrlShortenException.class, () -> {
                urlShortenService.getOriginalUrl(code);
            });
        }
    }
}
