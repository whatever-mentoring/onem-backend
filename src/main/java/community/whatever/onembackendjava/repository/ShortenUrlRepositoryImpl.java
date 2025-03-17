package community.whatever.onembackendjava.repository;

import community.whatever.onembackendjava.entity.ShortenUrl;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class ShortenUrlRepositoryImpl implements ShortenUrlRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<ShortenUrl> rowMapper = (rs, rowNum) -> ShortenUrl.builder()
            .id(rs.getLong("id"))
            .shortKey(rs.getString("short_key"))
            .originalUrl(rs.getString("original_url"))
            .createdAt(rs.getTimestamp("created_at").toInstant())
            .expiryTime(rs.getTimestamp("expiry_time").toInstant())
            .build();

    @Override
    public ShortenUrl save(ShortenUrl shortenUrl) {
        if (shortenUrl.getId() == null) {
            return insert(shortenUrl);
        } else {
            return update(shortenUrl);
        }
    }

    private ShortenUrl insert(ShortenUrl shortenUrl) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO shorten_urls (short_key, original_url, created_at, expiry_time) VALUES (?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, shortenUrl.getShortKey());
            ps.setString(2, shortenUrl.getOriginalUrl());
            ps.setTimestamp(3, Timestamp.from(shortenUrl.getCreatedAt() != null ? shortenUrl.getCreatedAt() : Instant.now()));
            ps.setTimestamp(4, Timestamp.from(shortenUrl.getExpiryTime()));
            return ps;
        }, keyHolder);

        Number key = null;
        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("id")) {
            key = (Number) keyHolder.getKeys().get("id");
        } else {
            key = keyHolder.getKey();
        }
        
        long id = Objects.requireNonNull(key).longValue();
        shortenUrl.setId(id);
        return shortenUrl;
    }

    private ShortenUrl update(ShortenUrl shortenUrl) {
        String sql = "UPDATE shorten_urls SET original_url = ?, expiry_time = ? WHERE id = ?";
        jdbcTemplate.update(sql, 
                shortenUrl.getOriginalUrl(), 
                Timestamp.from(shortenUrl.getExpiryTime()), 
                shortenUrl.getId());
        return shortenUrl;
    }

    @Override
    public Optional<ShortenUrl> findByShortKey(String shortKey) {
        try {
            String sql = "SELECT * FROM shorten_urls WHERE short_key = ?";
            ShortenUrl shortenUrl = jdbcTemplate.queryForObject(sql, rowMapper, shortKey);
            return Optional.ofNullable(shortenUrl);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ShortenUrl> findAllShortenUrls() {
        String sql = "SELECT * FROM shorten_urls";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public int deleteByShortKey(String shortKey) {
        String sql = "DELETE FROM shorten_urls WHERE short_key = ?";
        return jdbcTemplate.update(sql, shortKey);
    }
}
