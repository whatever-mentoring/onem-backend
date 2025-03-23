package community.whatever.onembackendkotlin.infra.repository

import community.whatever.onembackendkotlin.domain.BlockedDomain
import community.whatever.onembackendkotlin.domain.BlockedDomainRepository
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class BlockedDomainJdbcRepositoryImpl(
    private val jdbcClient: JdbcClient,
) : BlockedDomainRepository {

    override fun save(blockedDomain: BlockedDomain): BlockedDomain {
        val sql = """
            INSERT INTO BLOCKED_DOMAIN (id, domain) 
            VALUES (:id, :domain)
        """.trimIndent()

        jdbcClient.sql(sql)
            .param("id", blockedDomain.id)
            .param("domain", blockedDomain.domain)
            .update()
        return blockedDomain
    }

    override fun existsByDomain(domain: String): Boolean {
        val sql = """
            SELECT COUNT(*) FROM BLOCKED_DOMAIN 
            WHERE domain = :domain
        """.trimIndent()

        val single = jdbcClient.sql(sql)
            .param("domain", domain)
            .query(Int::class.java)
            .single()
        println("single: $single")
        return single > 0
    }

    override fun deleteByDomain(domain: String) {
        val sql = """
            DELETE FROM BLOCKED_DOMAIN 
            WHERE domain = :domain
        """.trimIndent()

        jdbcClient.sql(sql)
            .param("domain", domain)
            .update()
    }

    override fun findAll(): List<BlockedDomain> {
        val sql = """
            SELECT * FROM BLOCKED_DOMAIN
        """.trimIndent()

        return jdbcClient.sql(sql)
            .query { rs, _ ->
                BlockedDomain(rs.getString("domain"), UUID.fromString(rs.getString("id")))
            }
            .list()
    }
}
