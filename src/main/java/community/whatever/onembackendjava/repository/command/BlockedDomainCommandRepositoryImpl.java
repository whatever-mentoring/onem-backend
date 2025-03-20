package community.whatever.onembackendjava.repository.command;

import community.whatever.onembackendjava.entity.BlockedDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * BlockedDomainCommandRepository 인터페이스의 JDBC 구현체
 * 쓰기 작업만 담당하며 주 데이터베이스에 연결됩니다.
 */
@Repository
@RequiredArgsConstructor
public class BlockedDomainCommandRepositoryImpl implements BlockedDomainCommandRepository {

    private final JdbcTemplate primaryJdbcTemplate;

    @Override
    public boolean save(BlockedDomain blockedDomain) {
        String sql = "INSERT INTO blocked_domains (domain) VALUES (?) ON CONFLICT (domain) DO NOTHING";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        int rows = primaryJdbcTemplate.update(connection -> {
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
        int rows = primaryJdbcTemplate.update(sql, domain);
        return rows > 0;
    }
}
