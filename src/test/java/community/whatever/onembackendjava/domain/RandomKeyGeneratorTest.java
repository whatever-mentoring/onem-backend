package community.whatever.onembackendjava.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class RandomKeyGeneratorTest {

    private static final String TEST_ENV_PREFIX = "test";
    private static final int TEST_KEY_LENGTH = 6;

    @Test
    @DisplayName("생성된 랜덤 키는 올바른 형식(prefix-randomPart)을 가진다")
    void generate_ReturnsKeyWithCorrectFormat() {
        // given
        RandomKeyGenerator keyGenerator = new RandomKeyGenerator(TEST_ENV_PREFIX, TEST_KEY_LENGTH);
        Pattern pattern = Pattern.compile("^" + TEST_ENV_PREFIX + "-[A-Za-z0-9_-]{" + TEST_KEY_LENGTH + "}$");
        
        // when
        String randomKey = keyGenerator.generate();
        
        // then
        assertTrue(pattern.matcher(randomKey).matches(), 
                "Generated key should match the pattern: " + pattern.pattern() + ", but was: " + randomKey);
    }
    
    @Test
    @DisplayName("동일한 입력값으로 호출하면 동일한 키가 생성된다 (결정론적)")
    void generate_WithSameInputs_ReturnsSameKey() {
        // given
        RandomKeyGenerator keyGenerator = new RandomKeyGenerator(TEST_ENV_PREFIX, TEST_KEY_LENGTH);
        long timestamp = 1615432200000L; // 2021-03-11 05:30:00 UTC
        long randomValue = 123456789L;
        
        // when
        String key1 = keyGenerator.generate(timestamp, randomValue);
        String key2 = keyGenerator.generate(timestamp, randomValue);
        
        // then
        assertEquals(key1, key2, "Same inputs should produce the same key");
    }
    
    @Test
    @DisplayName("서로 다른 입력값으로 호출하면 서로 다른 키가 생성된다")
    void generate_WithDifferentInputs_ReturnsDifferentKeys() {
        // given
        RandomKeyGenerator keyGenerator = new RandomKeyGenerator(TEST_ENV_PREFIX, TEST_KEY_LENGTH);
        long timestamp1 = 1615432200000L; // 2021-03-11 05:30:00 UTC
        long timestamp2 = 1615432300000L; // 2021-03-11 05:31:40 UTC
        long randomValue = 123456789L;
        
        // when
        String key1 = keyGenerator.generate(timestamp1, randomValue);
        String key2 = keyGenerator.generate(timestamp2, randomValue);
        
        // then
        assertNotEquals(key1, key2, "Different inputs should produce different keys");
    }
    
    @Test
    @DisplayName("대량의 키를 생성해도 중복이 발생하지 않는다")
    void generate_WithMultipleKeys_NoDuplicates() {
        // given
        RandomKeyGenerator keyGenerator = new RandomKeyGenerator(TEST_ENV_PREFIX, TEST_KEY_LENGTH);
        int keyCount = 1000;
        Set<String> keys = new HashSet<>();
        
        // when
        for (int i = 0; i < keyCount; i++) {
            keys.add(keyGenerator.generate());
        }
        
        // then
        assertEquals(keyCount, keys.size(), "All generated keys should be unique");
    }
    
    @Test
    @DisplayName("커스텀 프리픽스와 키 길이로 생성된 키의 형식을 검증한다")
    void generate_WithCustomPrefixAndLength_ReturnsCorrectFormat() {
        // given
        String customPrefix = "custom";
        int customLength = 8;
        RandomKeyGenerator keyGenerator = new RandomKeyGenerator(customPrefix, customLength);
        Pattern pattern = Pattern.compile("^" + customPrefix + "-[A-Za-z0-9_-]{" + customLength + "}$");
        
        // when
        String randomKey = keyGenerator.generate();
        
        // then
        assertTrue(pattern.matcher(randomKey).matches(), 
                "Generated key should match the pattern: " + pattern.pattern() + ", but was: " + randomKey);
        assertTrue(randomKey.startsWith(customPrefix + "-"), "생성된 키는 프리픽스로 시작해야 한다");
        assertEquals(customPrefix.length() + 1 + customLength, randomKey.length(), "생성된 키의 길이가 올바른지 확인");
    }
}
