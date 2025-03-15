package community.whatever.onembackendjava.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 차단된 도메인 엔티티
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlockedDomain {
    private Long id;
    private String domain;
    private Instant createdAt;
}
