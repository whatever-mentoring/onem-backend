package community.whatever.onembackendjava.repository.query;

import community.whatever.onembackendjava.entity.BlockedDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BlockedDomainQueryRepository 인터페이스의 JDBC 구현체
 * 읽기 작업만 담당하며 레플리카 데이터베이스에 연결됩니다.
 */
@Repository
@RequiredArgsConstructor
public class BlockedDomainQueryRepositoryImpl implements BlockedDomainQueryRepository {

    @Qualifier("replicaJdbcTemplate")
    private final JdbcTemplate replicaJdbcTemplate;
    
    private final RowMapper<BlockedDomain> rowMapper = (rs, rowNum) -> 
        BlockedDomain.builder()
            .id(rs.getLong("id"))
            .domain(rs.getString("domain"))
            .createdAt(rs.getTimestamp("created_at").toInstant())
            .build();
    
    @Override
    public List<BlockedDomain> findAllDomains() {
        String sql = "SELECT * FROM blocked_domains";
        return replicaJdbcTemplate.query(sql, rowMapper);
    }
    
    @Override
    public boolean exists(String domain) {
        String sql = "SELECT COUNT(*) FROM blocked_domains WHERE domain = ?";
        Integer count = replicaJdbcTemplate.queryForObject(sql, Integer.class, domain);
        return count != null && count > 0;
    }
}
