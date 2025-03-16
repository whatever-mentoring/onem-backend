package community.whatever.onembackendjava.repository;

import community.whatever.onembackendjava.entity.BlockedDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * BlockedDomainRepository 인터페이스의 JDBC 구현체
 */
@Repository
@RequiredArgsConstructor
public class BlockedDomainRepositoryImpl implements BlockedDomainRepository {

    private final JdbcTemplate jdbcTemplate;
    
    private final RowMapper<BlockedDomain> rowMapper = (rs, rowNum) -> 
        BlockedDomain.builder()
            .id(rs.getLong("id"))
            .domain(rs.getString("domain"))
            .createdAt(rs.getTimestamp("created_at").toInstant())
            .build();

    @Override
    public boolean save(BlockedDomain blockedDomain) {
        String sql = "INSERT INTO blocked_domains (domain) VALUES (?) ON CONFLICT (domain) DO NOTHING";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        int rows = jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, blockedDomain.getDomain());
            return ps;
        }, keyHolder);
        
        if (rows > 0 && keyHolder.getKeys() != null) {
            if (keyHolder.getKeys().containsKey("id")) {
                blockedDomain.setId(((Number) keyHolder.getKeys().get("id")).longValue());
            } else if (keyHolder.getKey() != null) {
                blockedDomain.setId(keyHolder.getKey().longValue());
            }
        }
        
        return rows > 0;
    }
    
    @Override
    public boolean delete(String domain) {
        String sql = "DELETE FROM blocked_domains WHERE domain = ?";
        int rows = jdbcTemplate.update(sql, domain);
        return rows > 0;
    }
    
    @Override
    public Set<String> findAll() {
        return findAllDomains().stream()
                .map(BlockedDomain::getDomain)
                .collect(Collectors.toSet());
    }
    
    @Override
    public List<BlockedDomain> findAllDomains() {
        String sql = "SELECT * FROM blocked_domains";
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    @Override
    public boolean exists(String domain) {
        String sql = "SELECT COUNT(*) FROM blocked_domains WHERE domain = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, domain);
        return count != null && count > 0;
    }
}
