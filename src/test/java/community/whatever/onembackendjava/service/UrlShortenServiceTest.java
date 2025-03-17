package community.whatever.onembackendjava.service;

import community.whatever.onembackendjava.constant.AppEnvironment;
import community.whatever.onembackendjava.constant.UrlConstants;
import community.whatever.onembackendjava.repository.ShortenUrlRepository;
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
    private BlockedDomainService blockedDomainService;
    
    @Mock
    private AppEnvironment appEnvironment;
    
    @Mock
    private ShortenUrlRepository shortenUrlDao;

    private UrlShortenService urlShortenService;
    
    private final String TEST_PREFIX = "dev";
    
    @BeforeEach
    void setUp() {
        when(appEnvironment.getPrefix()).thenReturn(TEST_PREFIX);
        urlShortenService = new UrlShortenService(blockedDomainService, appEnvironment, shortenUrlDao);
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
            
            // URL 차단 확인을 모킹
            when(blockedDomainService.isUrlBlocked(eq(originUrl))).thenReturn(true);

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.createShortenUrl(request);
            });
            
            verify(blockedDomainService).isUrlBlocked(eq(originUrl));
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
        @DisplayName("존재하지 않는 URL을 조회하면 예외가 발생한다")
        void searchShortenUrl_NotFound_ThrowsException() {
            // given
            String key = TEST_PREFIX + "ABC";
            SearchShortenUrlRequest request = new SearchShortenUrlRequest(key);
            
            // DB에 URL이 존재하지 않음을 시뮬레이션
            when(shortenUrlDao.findByShortKey(eq(key))).thenReturn(Optional.empty());

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.searchShortenUrl(request);
            });
            
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            verify(shortenUrlDao).findByShortKey(eq(key));
        }
        
        @Test
        @DisplayName("만료된 URL을 조회하면 예외가 발생한다")
        void searchShortenUrl_Expired_ThrowsException() {
            // given
            String key = TEST_PREFIX + "ABC";
            SearchShortenUrlRequest request = new SearchShortenUrlRequest(key);
            
            // 만료된 URL 시뮬레이션
            ShortenUrl expiredUrl = ShortenUrl.builder()
                    .shortKey(key)
                    .originalUrl("https://example.com")
                    .createdAt(Instant.now().minusSeconds(7200))  // 2시간 전
                    .expiryTime(Instant.now().minusSeconds(3600)) // 1시간 전 만료
                    .build();
            
            when(shortenUrlDao.findByShortKey(eq(key))).thenReturn(Optional.of(expiredUrl));

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.searchShortenUrl(request);
            });
            
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
            verify(shortenUrlDao).findByShortKey(eq(key));
            assertTrue(exception.getMessage().contains("expired"));
        }
        
        @Test
        @DisplayName("유효한 URL을 조회하면 원본 URL을 반환한다")
        void searchShortenUrl_Valid_ReturnsOriginalUrl() {
            // given
            String key = TEST_PREFIX + "ABC";
            String originalUrl = "https://example.com";
            SearchShortenUrlRequest request = new SearchShortenUrlRequest(key);
            
            // 유효한 URL 시뮬레이션
            ShortenUrl validUrl = ShortenUrl.builder()
                    .shortKey(key)
                    .originalUrl(originalUrl)
                    .createdAt(Instant.now().minusSeconds(3600))  // 1시간 전
                    .expiryTime(Instant.now().plusSeconds(3600))  // 1시간 후 만료
                    .build();
            
            when(shortenUrlDao.findByShortKey(eq(key))).thenReturn(Optional.of(validUrl));

            // when
            SearchShortenUrlResponse response = urlShortenService.searchShortenUrl(request);

            // then
            assertNotNull(response);
            assertEquals(originalUrl, response.originUrl());
            verify(shortenUrlDao).findByShortKey(eq(key));
        }
    }

    @Nested
    @DisplayName("Original URL 조회 테스트")
    class GetOriginalUrlTest {
        
        @Test
        @DisplayName("잘못된 프리픽스로 URL을 조회하면 예외가 발생한다")
        void getOriginalUrl_InvalidPrefix_ThrowsException() {
            // given
            String invalidKey = "invalid-key";
            
            // 환경 정보 시뮬레이션
            when(appEnvironment.name()).thenReturn("DEV");

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.getOriginalUrl(invalidKey);
            });
            
            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
            assertTrue(exception.getMessage().contains("적합하지 않은 환경"));
        }
        
        @Test
        @DisplayName("존재하지 않는 URL을 조회하면 예외가 발생한다")
        void getOriginalUrl_NotFound_ThrowsException() {
            // given
            String key = TEST_PREFIX + "ABC";
            
            // DB에 URL이 존재하지 않음을 시뮬레이션
            when(shortenUrlDao.findByShortKey(eq(key))).thenReturn(Optional.empty());

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.getOriginalUrl(key);
            });
            
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            verify(shortenUrlDao).findByShortKey(eq(key));
        }
        
        @Test
        @DisplayName("만료된 URL을 조회하면 예외가 발생하고 DB에서 삭제된다")
        void getOriginalUrl_Expired_ThrowsExceptionAndDeletesFromDB() {
            // given
            String key = TEST_PREFIX + "ABC";
            
            // 만료된 URL 시뮬레이션
            ShortenUrl expiredUrl = ShortenUrl.builder()
                    .shortKey(key)
                    .originalUrl("https://example.com")
                    .createdAt(Instant.now().minusSeconds(7200))  // 2시간 전
                    .expiryTime(Instant.now().minusSeconds(3600)) // 1시간 전 만료
                    .build();
            
            when(shortenUrlDao.findByShortKey(eq(key))).thenReturn(Optional.of(expiredUrl));
            when(shortenUrlDao.deleteByShortKey(eq(key))).thenReturn(1);

            // when & then
            UrlShortenException exception = assertThrows(UrlShortenException.class, () -> {
                urlShortenService.getOriginalUrl(key);
            });
            
            assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
            verify(shortenUrlDao).findByShortKey(eq(key));
            verify(shortenUrlDao).deleteByShortKey(eq(key));
        }
        
        @Test
        @DisplayName("유효한 URL을 조회하면 원본 URL을 반환한다")
        void getOriginalUrl_Valid_ReturnsOriginalUrl() {
            // given
            String key = TEST_PREFIX + "ABC";
            String originalUrl = "https://example.com";
            
            // 유효한 URL 시뮬레이션
            ShortenUrl validUrl = ShortenUrl.builder()
                    .shortKey(key)
                    .originalUrl(originalUrl)
                    .createdAt(Instant.now().minusSeconds(3600))  // 1시간 전
                    .expiryTime(Instant.now().plusSeconds(3600))  // 1시간 후 만료
                    .build();
            
            when(shortenUrlDao.findByShortKey(eq(key))).thenReturn(Optional.of(validUrl));

            // when
            String result = urlShortenService.getOriginalUrl(key);

            // then
            assertEquals(originalUrl, result);
            verify(shortenUrlDao).findByShortKey(eq(key));
        }
    }
}
