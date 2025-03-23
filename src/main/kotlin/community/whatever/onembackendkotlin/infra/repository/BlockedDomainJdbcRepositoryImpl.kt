package community.whatever.onembackendkotlin.infra.repository

import community.whatever.onembackendkotlin.domain.BlockedDomain
import community.whatever.onembackendkotlin.domain.BlockedDomainRepository
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class BlockedDomainJdbcRepositoryImpl(
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
) : BlockedDomainRepository {

    override fun save(blockedDomain: BlockedDomain): BlockedDomain {
        val sql = "INSERT INTO BLOCKED_DOMAIN (id, domain) VALUES (:id, :domain)"

        val params = MapSqlParameterSource()
            .addValue("id", blockedDomain.id)
            .addValue("domain", blockedDomain.domain)

        namedParameterJdbcTemplate.update(sql, params)
        return blockedDomain.copy(id = blockedDomain.id)
    }

    override fun existsByDomain(domain: String): Boolean {
        val sql = "SELECT COUNT(*) FROM BLOCKED_DOMAIN WHERE domain = :domain"

        val params = MapSqlParameterSource()
            .addValue("domain", domain)

        val count = namedParameterJdbcTemplate.queryForObject(sql, params, Int::class.java) ?: 0
        return count > 0
    }

    override fun deleteByDomain(domain: String) {
        val sql = "DELETE FROM BLOCKED_DOMAIN WHERE domain = :domain"

        val params = MapSqlParameterSource()
            .addValue("domain", domain)

        namedParameterJdbcTemplate.update(sql, params)
    }

    override fun findAll(): List<BlockedDomain> {
        val sql = "SELECT * FROM BLOCKED_DOMAIN"

        return namedParameterJdbcTemplate.query(sql) { rs, _ ->
            BlockedDomain(rs.getString("domain"), UUID.fromString(rs.getString("id")))
        }
    }
}
