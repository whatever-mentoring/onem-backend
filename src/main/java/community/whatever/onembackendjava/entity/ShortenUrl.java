package community.whatever.onembackendjava.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 단축 URL 엔티티
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortenUrl {
    private Long id;
    private String shortKey;
    private String originalUrl;
    private Instant createdAt;
    private Instant expiryTime;
}
