package community.whatever.onembackendjava.util;

import community.whatever.onembackendjava.entity.ShortenUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 더미 데이터 생성을 위한 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
public class DummyDataGenerator {

    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();
    
    // 도메인 목록 (랜덤하게 선택할 예정)
    private static final String[] DOMAINS = {
            "example.com", "test.com", "dummy.org", "sample.net", "demo.io",
            "github.com", "stackoverflow.com", "google.com", "naver.com", "daum.net"
    };
    
    // 경로 목록 (랜덤하게 선택할 예정)
    private static final String[] PATHS = {
            "home", "about", "product", "service", "contact", 
            "blog", "news", "gallery", "login", "signup",
            "faq", "support", "download", "pricing", "team",
            "portfolio", "projects", "testimonials", "events", "careers"
    };
    
    /**
     * 지정된 개수만큼 더미 단축 URL 데이터를 생성하여 데이터베이스에 삽입합니다.
     * 
     * @param count 생성할 더미 데이터 개수
     */
    @Transactional
    public void generateDummyUrls(int count) {
        long startTime = System.currentTimeMillis();
        System.out.println("더미 데이터 생성 시작: " + count + "개");
        
        // 배치 크기 (한 번에 처리할 레코드 수)
        final int batchSize = 1000;
        
        for (int i = 0; i < count; i += batchSize) {
            int currentBatchSize = Math.min(batchSize, count - i);
            insertBatch(currentBatchSize, i);
            
            // 진행 상태 표시
            if ((i + batchSize) % 10000 == 0 || (i + batchSize) >= count) {
                System.out.println("처리 완료: " + Math.min(i + batchSize, count) + "/" + count);
            }
        }
        
        long endTime = System.currentTimeMillis();
        double seconds = (endTime - startTime) / 1000.0;
        System.out.println("더미 데이터 생성 완료: " + count + "개 (" + seconds + "초)");
    }
    
    /**
     * 배치 방식으로 데이터를 삽입합니다.
     * 
     * @param batchSize 배치 크기
     * @param offset 오프셋 (현재까지 생성된 레코드 수)
     */
    private void insertBatch(int batchSize, int offset) {
        String sql = "INSERT INTO shorten_urls (short_key, original_url, created_at, expiry_time) VALUES (?, ?, ?, ?)";
        
        List<Object[]> batchArgs = new ArrayList<>(batchSize);
        
        for (int i = 0; i < batchSize; i++) {
            // 고유한 단축 키 생성 (6~8자리 영숫자)
            String shortKey = generateUniqueShortKey(6 + random.nextInt(3));
            
            // 원본 URL 생성
            String originalUrl = generateRandomUrl();
            
            // 생성 시간 설정 (현재 시간 ~ 30일 전 사이 랜덤)
            Instant createdAt = Instant.now().minus(random.nextInt(30), ChronoUnit.DAYS)
                    .minus(random.nextInt(24), ChronoUnit.HOURS)
                    .minus(random.nextInt(60), ChronoUnit.MINUTES);
            
            // 만료 시간 설정 (생성 시간 + 1~365일 사이 랜덤)
            Instant expiryTime = createdAt.plus(1 + random.nextInt(365), ChronoUnit.DAYS);
            
            batchArgs.add(new Object[]{
                    shortKey,
                    originalUrl,
                    Timestamp.from(createdAt),
                    Timestamp.from(expiryTime)
            });
        }
        
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }
    
    /**
     * 지정된 길이의 고유한 단축 키를 생성합니다.
     * 
     * @param length 생성할 키의 길이
     * @return 생성된 단축 키
     */
    private String generateUniqueShortKey(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }
    
    /**
     * 랜덤한 URL을 생성합니다.
     * 
     * @return 생성된 URL
     */
    private String generateRandomUrl() {
        String domain = DOMAINS[random.nextInt(DOMAINS.length)];
        StringBuilder url = new StringBuilder("https://").append(domain);
        
        // 1~3개의 경로 추가
        int pathCount = 1 + random.nextInt(3);
        for (int i = 0; i < pathCount; i++) {
            url.append("/").append(PATHS[random.nextInt(PATHS.length)]);
        }
        
        // 쿼리 파라미터 추가 (30% 확률)
        if (random.nextInt(10) < 3) {
            url.append("?id=").append(random.nextInt(10000));
            
            // 추가 파라미터 (50% 확률)
            if (random.nextInt(2) == 0) {
                url.append("&type=").append(UUID.randomUUID().toString().substring(0, 8));
            }
        }
        
        return url.toString();
    }
}
