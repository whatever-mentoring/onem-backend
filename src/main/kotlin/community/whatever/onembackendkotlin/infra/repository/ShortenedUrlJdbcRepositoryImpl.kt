package community.whatever.onembackendkotlin.infra.repository

import community.whatever.onembackendkotlin.domain.ShortenedUrl
import community.whatever.onembackendkotlin.domain.ShortenedUrlRepository
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime

@Repository
class ShortenedUrlJdbcRepositoryImpl(
    private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
    private val idGeneration: ShortenUrlIdGeneration,
) : ShortenedUrlRepository {

    override fun findById(id: String): ShortenedUrl? {
        val sql = """
            SELECT * FROM shortened_url 
            WHERE id = :id
        """.trimIndent()

        val params = mapOf("id" to id)

        return namedParameterJdbcTemplate.query(sql, params) { rs, _ -> mapToShortenedUrl(rs) }.firstOrNull()
    }

    override fun findByIdAndDeletedIsFalse(id: String): ShortenedUrl? {
        val sql = """
            SELECT * FROM shortened_url 
            WHERE id = :id AND deleted = FALSE
        """.trimIndent()

        val params = mapOf("id" to id)

        return namedParameterJdbcTemplate.query(sql, params) { rs, _ -> mapToShortenedUrl(rs) }.firstOrNull()
    }

    override fun save(shortenedUrl: ShortenedUrl): ShortenedUrl {
        val id = if (shortenedUrl.id.isNullOrBlank()) {
            idGeneration.generateId()
        } else {
            shortenedUrl.id
        }

        if (findById(id) != null) {
            val updateSql = """
                UPDATE shortened_url 
                SET origin_url = :originUrl, expired_at = :expiredAt, deleted = :deleted 
                WHERE id = :id
            """.trimIndent()

            val params = mapOf(
                "id" to id,
                "originUrl" to shortenedUrl.originUrl,
                "expiredAt" to shortenedUrl.expiredAt,
                "deleted" to shortenedUrl.deleted
            )

            namedParameterJdbcTemplate.update(updateSql, params)
        } else {
            val insertSql = """
                INSERT INTO shortened_url (id, origin_url, expired_at, deleted) 
                VALUES (:id, :originUrl, :expiredAt, :deleted)
            """.trimIndent()

            val params = mapOf(
                "id" to id,
                "originUrl" to shortenedUrl.originUrl,
                "expiredAt" to shortenedUrl.expiredAt,
                "deleted" to shortenedUrl.deleted
            )

            namedParameterJdbcTemplate.update(insertSql, params)
        }

        return shortenedUrl.copy(id = id)
    }

    override fun existsByOriginUrl(originUrl: String): Boolean {
        val sql = """
            SELECT COUNT(*) FROM shortened_url 
            WHERE origin_url = :originUrl
            """.trimIndent()

        val params = mapOf("originUrl" to originUrl)

        val count = namedParameterJdbcTemplate.queryForObject(sql, params, Int::class.java) ?: 0
        return count > 0
    }

    override fun findByOriginUrl(originUrl: String): ShortenedUrl? {
        val sql = """
            SELECT * FROM shortened_url 
            WHERE origin_url = :originUrl AND deleted = FALSE
            """.trimIndent()

        val params = mapOf("originUrl" to originUrl)

        return namedParameterJdbcTemplate.query(sql, params) { rs, _ -> mapToShortenedUrl(rs) }.firstOrNull()
    }

    override fun deleteAll() {
        val sql = """
            UPDATE shortened_url 
            SET deleted = TRUE
            WHERE deleted = FALSE
            """.trimIndent()
        namedParameterJdbcTemplate.update(sql, emptyMap<String, Any>())
    }

    override fun deleteAllByExpiredAtBefore(baseTime: LocalDateTime) {
        val sql = """
            UPDATE shortened_url 
            SET deleted = TRUE 
            WHERE expired_at < :baseTime
            """.trimIndent()

        val params = mapOf("baseTime" to baseTime)

        namedParameterJdbcTemplate.update(sql, params)
    }

    private fun mapToShortenedUrl(rs: ResultSet): ShortenedUrl {
        return ShortenedUrl(
            id = rs.getString("id"),
            originUrl = rs.getString("origin_url"),
            expiredAt = rs.getTimestamp("expired_at").toLocalDateTime(),
            deleted = rs.getBoolean("deleted")
        )
    }
}
