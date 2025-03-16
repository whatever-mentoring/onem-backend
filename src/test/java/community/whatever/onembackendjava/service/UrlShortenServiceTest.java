package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.DomainBlockingManager;
import community.whatever.onembackendjava.constant.AppEnvironment;
import community.whatever.onembackendjava.constant.UrlConstants;
import community.whatever.onembackendjava.dao.ShortenUrlRepository;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UrlShortenServiceTest {

    @Mock
    private DomainBlockingManager urlMappingManager;
    
    @Mock
    private AppEnvironment appEnvironment;
    
    @Mock
    private ShortenUrlRepository shortenUrlDao;

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
            when(shortenUrlDao.save(any(ShortenUrl.class))).thenReturn(new ShortenUrl());
            
            // when
            CreateShortenUrlResponse response = urlShortenService.createShortenUrl(request);

            // then
            assertNotNull(response);
            ArgumentCaptor<ShortenUrl> urlCaptor = ArgumentCaptor.forClass(ShortenUrl.class);
            verify(shortenUrlDao).save(urlCaptor.capture());
            
            ShortenUrl savedUrl = urlCaptor.getValue();
            assertEquals(originUrl, savedUrl.getOriginalUrl());
            assertTrue(savedUrl.getExpiryTime().isAfter(Instant.now()));
            assertEquals(UrlConstants.DEFAULT_TTL_MINUTES * 60, 
                    savedUrl.getExpiryTime().getEpochSecond() - savedUrl.getCreatedAt().getEpochSecond(), 10);
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
            when(shortenUrlDao.save(any(ShortenUrl.class))).thenReturn(new ShortenUrl());

            // when
            CreateShortenUrlResponse response = urlShortenService.createShortenUrl(request);

            // then
            assertNotNull(response);
            ArgumentCaptor<ShortenUrl> urlCaptor = ArgumentCaptor.forClass(ShortenUrl.class);
            verify(shortenUrlDao).save(urlCaptor.capture());
            
            ShortenUrl savedUrl = urlCaptor.getValue();
            assertEquals(originUrl, savedUrl.getOriginalUrl());
            assertTrue(savedUrl.getExpiryTime().isAfter(Instant.now()));
            assertEquals(customTTL * 60, 
                    savedUrl.getExpiryTime().getEpochSecond() - savedUrl.getCreatedAt().getEpochSecond(), 10);
        }
        
        @Test
        @DisplayName("URL에 스키마가 없으면 https://를 추가한다")
        void createShortenUrl_AddsHttpsSchemeWhenMissing() {
            // given
            String originUrl = "example.com";
            String expectedUrl = "https://example.com";
            CreateShortenUrlRequest request = new CreateShortenUrlRequest(originUrl, null);

            // DB에 URL이 존재하지 않음을 시뮬레이션
            when(shortenUrlDao.findByShortKey(anyString())).thenReturn(Optional.empty());
            when(shortenUrlDao.save(any(ShortenUrl.class))).thenReturn(new ShortenUrl());

            // when
            CreateShortenUrlResponse response = urlShortenService.createShortenUrl(request);

            // then
            assertNotNull(response);
            ArgumentCaptor<ShortenUrl> urlCaptor = ArgumentCaptor.forClass(ShortenUrl.class);
            verify(shortenUrlDao).save(urlCaptor.capture());
            
            ShortenUrl savedUrl = urlCaptor.getValue();
            assertEquals(expectedUrl, savedUrl.getOriginalUrl());
        }
        
        @Test
        @DisplayName("차단된 도메인으로 URL을 생성하면 예외가 발생한다")
        void createShortenUrl_WithBlockedDomain_ThrowsException() {
            // given
            String originUrl = "https://blocked-example.com";
            CreateShortenUrlRequest request = new CreateShortenUrlRequest(originUrl, null);
            
            // DomainBlockingManager는 urlMappingManager 변수명으로 저장되어 있음
            when(urlMappingManager.isUrlBlocked(eq("blocked-example.com"))).thenReturn(true);

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.createShortenUrl(request);
            });
            
            verify(urlMappingManager).isUrlBlocked(eq("blocked-example.com"));
            assertTrue(exception.getMessage().contains("blocked-example.com"));
        }
        
        @Test
        @DisplayName("중복 키가 존재하면 새로운 키를 생성하여 재시도한다")
        void createShortenUrl_DuplicateKey_RetrySuccess() {
            // given
            String originUrl = "https://example.com";
            CreateShortenUrlRequest request = new CreateShortenUrlRequest(originUrl, null);

            // 첫 번째 키는 이미 존재하고, 두 번째 키는 존재하지 않음
            when(shortenUrlDao.findByShortKey(anyString()))
                    .thenReturn(Optional.of(new ShortenUrl())) // 첫 번째 조회
                    .thenReturn(Optional.empty());           // 두 번째 조회
            
            when(shortenUrlDao.save(any(ShortenUrl.class))).thenReturn(new ShortenUrl());

            // when
            CreateShortenUrlResponse response = urlShortenService.createShortenUrl(request);

            // then
            assertNotNull(response);
            verify(shortenUrlDao, times(2)).findByShortKey(anyString());
            verify(shortenUrlDao).save(any(ShortenUrl.class));
        }
    }

    @Nested
    @DisplayName("URL 조회 테스트")
    class SearchShortenUrlTest {
        
        @Test
        @DisplayName("잘못된 프리픽스로 URL을 조회하면 예외가 발생한다")
        void searchShortenUrl_InvalidPrefix_ThrowsException() {
            // given
            String invalidKey = "invalid-key";
            SearchShortenUrlRequest request = new SearchShortenUrlRequest(invalidKey);
            
            // 환경 정보 시뮬레이션
            when(appEnvironment.name()).thenReturn("DEV");

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.searchShortenUrl(request);
            });
            
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
            assertTrue(exception.getMessage().contains("적합하지 않은 환경"));
        }
        
        @Test
        @DisplayName("존재하는 URL을 조회할 수 있다")
        void searchShortenUrl_ExistingKey_Success() {
            // given
            String key = TEST_PREFIX + "-abc123";
            String url = "https://example.com";
            
            // DB에 URL이 존재함을 시뮬레이션
            ShortenUrl shortenUrl = ShortenUrl.builder()
                    .shortKey(key)
                    .originalUrl(url)
                    .createdAt(Instant.now())
                    .expiryTime(Instant.now().plusSeconds(3600))
                    .build();
            
            when(shortenUrlDao.findByShortKey(key)).thenReturn(Optional.of(shortenUrl));
            
            SearchShortenUrlRequest request = new SearchShortenUrlRequest(key);

            // when
            SearchShortenUrlResponse response = urlShortenService.searchShortenUrl(request);

            // then
            assertNotNull(response);
            assertEquals(url, response.originUrl());
        }
        
        @Test
        @DisplayName("만료된 URL을 조회하면 예외가 발생한다")
        void searchShortenUrl_ExpiredUrl_ThrowsException() {
            // given
            String key = TEST_PREFIX + "-abc123";
            String url = "https://example.com";
            
            // 만료된 URL 시뮬레이션
            ShortenUrl shortenUrl = ShortenUrl.builder()
                    .shortKey(key)
                    .originalUrl(url)
                    .createdAt(Instant.now().minusSeconds(7200))
                    .expiryTime(Instant.now().minusSeconds(3600))
                    .build();
            
            when(shortenUrlDao.findByShortKey(key)).thenReturn(Optional.of(shortenUrl));
            
            SearchShortenUrlRequest request = new SearchShortenUrlRequest(key);

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.searchShortenUrl(request);
            });
            
            assertTrue(exception.getMessage().contains("expired"));
        }
        
        @Test
        @DisplayName("존재하지 않는 URL을 조회하면 예외가 발생한다")
        void searchShortenUrl_NonExistingKey_ThrowsException() {
            // given
            String key = TEST_PREFIX + "-nonexist";
            SearchShortenUrlRequest request = new SearchShortenUrlRequest(key);
            
            // DB에 URL이 존재하지 않음을 시뮬레이션
            when(shortenUrlDao.findByShortKey(key)).thenReturn(Optional.empty());

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.searchShortenUrl(request);
            });
            
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        }
    }
    
    @Nested
    @DisplayName("원본 URL 조회 테스트")
    class GetOriginalUrlTest {
        
        @Test
        @DisplayName("잘못된 프리픽스로 URL을 조회하면 예외가 발생한다")
        void getOriginalUrl_InvalidPrefix_ThrowsException() {
            // given
            String invalidCode = "invalid-code";
            
            // 환경 정보 시뮬레이션
            when(appEnvironment.name()).thenReturn("DEV");

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.getOriginalUrl(invalidCode);
            });
            
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
            assertTrue(exception.getMessage().contains("적합하지 않은 환경"));
        }
        
        @Test
        @DisplayName("존재하는 코드로 원본 URL을 조회할 수 있다")
        void getOriginalUrl_ExistingCode_Success() {
            // given
            String code = TEST_PREFIX + "-abc123";
            String url = "https://example.com";
            
            // DB에 URL이 존재함을 시뮬레이션
            ShortenUrl shortenUrl = ShortenUrl.builder()
                    .shortKey(code)
                    .originalUrl(url)
                    .createdAt(Instant.now())
                    .expiryTime(Instant.now().plusSeconds(3600))
                    .build();
            
            when(shortenUrlDao.findByShortKey(code)).thenReturn(Optional.of(shortenUrl));

            // when
            String originalUrl = urlShortenService.getOriginalUrl(code);

            // then
            assertEquals(url, originalUrl);
        }
        
        @Test
        @DisplayName("만료된 코드로 조회하면 예외가 발생하고 DB에서 삭제된다")
        void getOriginalUrl_ExpiredCode_ThrowsExceptionAndDeletesFromDb() {
            // given
            String code = TEST_PREFIX + "-abc123";
            String url = "https://example.com";
            
            // 만료된 URL 시뮬레이션
            ShortenUrl shortenUrl = ShortenUrl.builder()
                    .shortKey(code)
                    .originalUrl(url)
                    .createdAt(Instant.now().minusSeconds(7200))
                    .expiryTime(Instant.now().minusSeconds(3600))
                    .build();
            
            when(shortenUrlDao.findByShortKey(code)).thenReturn(Optional.of(shortenUrl));
            when(shortenUrlDao.deleteByShortKey(code)).thenReturn(1);

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.getOriginalUrl(code);
            });
            
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            verify(shortenUrlDao).deleteByShortKey(code);
        }
        
        @Test
        @DisplayName("존재하지 않는 코드로 조회하면 예외가 발생한다")
        void getOriginalUrl_NonExistingCode_ThrowsException() {
            // given
            String code = TEST_PREFIX + "-nonexist";
            
            // DB에 URL이 존재하지 않음을 시뮬레이션
            when(shortenUrlDao.findByShortKey(code)).thenReturn(Optional.empty());

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.getOriginalUrl(code);
            });
            
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        }
    }
}
