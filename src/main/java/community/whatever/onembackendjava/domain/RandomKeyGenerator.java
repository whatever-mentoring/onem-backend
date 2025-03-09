package community.whatever.onembackendjava.domain;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 랜덤 키 생성을 담당하는 도메인 클래스
 * 단일 책임 원칙(SRP)에 따라 키 생성 로직만 담당
 */
public class RandomKeyGenerator {
    
    private final String environmentPrefix;
    private final int keyLength;
    
    /**
     * 환경 접두사와 키 길이를 지정하여 RandomKeyGenerator를 생성합니다.
     * 
     * @param environmentPrefix 환경별 접두사 (예: dev, prod)
     * @param keyLength 생성할 랜덤 키의 길이
     */
    public RandomKeyGenerator(String environmentPrefix, int keyLength) {
        this.environmentPrefix = environmentPrefix;
        this.keyLength = keyLength;
    }
    
    /**
     * 현재 시간과 랜덤 값을 사용하여 새로운 랜덤 키를 생성합니다.
     * 
     * @return 생성된 랜덤 키 (형식: {environmentPrefix}-{randomPart})
     */
    public String generate() {
        long timestamp = Instant.now().toEpochMilli();
        long random = ThreadLocalRandom.current().nextLong();
        
        return generate(timestamp, random);
    }
    
    /**
     * 주어진 타임스탬프와 랜덤 값을 사용하여 랜덤 키를 생성합니다.
     * 테스트하기 쉽도록 외부에서 입력 값을 주입할 수 있는 메서드입니다.
     * 
     * @param timestamp 타임스탬프 (밀리초)
     * @param randomValue 랜덤 값
     * @return 생성된 랜덤 키 (형식: {environmentPrefix}-{randomPart})
     */
    public String generate(long timestamp, long randomValue) {
        String combined = timestamp + ":" + randomValue;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            String encoded = Base64.getUrlEncoder().encodeToString(hash);
            String randomPart = encoded.substring(0, keyLength);
            
            return String.format("%s-%s", environmentPrefix, randomPart);
        } catch (NoSuchAlgorithmException e) {
            System.err.println("SHA-256 알고리즘을 사용할 수 없습니다: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
