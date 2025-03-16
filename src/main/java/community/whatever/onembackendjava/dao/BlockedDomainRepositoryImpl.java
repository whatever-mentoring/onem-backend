package community.whatever.onembackendjava.dao;

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
        
        return rows > 0;
    }
    
    @Override
    public boolean delete(String domain) {
        String sql = "DELETE FROM blocked_domains WHERE domain = ?";
        int rows = jdbcTemplate.update(sql, domain);
        return rows > 0;
    }
    
    @Override
    public List<BlockedDomain> findAll() {
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
