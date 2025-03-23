package community.whatever.onembackendkotlin.infra.repository

import community.whatever.onembackendkotlin.domain.ShortenedUrl
import community.whatever.onembackendkotlin.domain.ShortenedUrlRepository
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.time.LocalDateTime
import java.util.Optional

@Repository
class ShortenedUrlJdbcRepositoryImpl(
    private val jdbcClient: JdbcClient,
) : ShortenedUrlRepository {

    override fun findById(id: String): Optional<ShortenedUrl> {
        val sql = """
            SELECT * FROM shortened_url 
            WHERE id = :id
        """.trimIndent()

        return jdbcClient.sql(sql)
            .param("id", id)
            .query { rs, _ -> mapToShortenedUrl(rs) }
            .optional()
    }

    override fun findByIdAndDeletedIsFalse(id: String): Optional<ShortenedUrl> {
        val sql = """
            SELECT * FROM shortened_url 
            WHERE id = :id AND deleted = FALSE
        """.trimIndent()

        return jdbcClient.sql(sql)
            .param("id", id)
            .query { rs, _ -> mapToShortenedUrl(rs) }
            .optional()
    }

    override fun save(shortenedUrl: ShortenedUrl): ShortenedUrl {
        val exists = existsByOriginUrl(shortenedUrl.originUrl)
        val sql = if (exists) {
            """
            UPDATE shortened_url 
            SET deleted = FALSE, expired_at = :expiredAt
            WHERE origin_url = :originUrl
            """.trimIndent()
        } else {
            """
            INSERT INTO shortened_url (id, origin_url, expired_at, deleted)
            VALUES (:id, :originUrl, :expiredAt, :deleted)
            """.trimIndent()
        }

        jdbcClient.sql(sql)
            .param("id", shortenedUrl.id)
            .param("originUrl", shortenedUrl.originUrl)
            .param("expiredAt", shortenedUrl.expiredAt)
            .param("deleted", shortenedUrl.deleted)
            .update()

        return shortenedUrl
    }

    override fun existsByOriginUrl(originUrl: String): Boolean {
        val sql = """
            SELECT COUNT(*) FROM shortened_url 
            WHERE origin_url = :originUrl
            """.trimIndent()

        return jdbcClient.sql(sql)
            .param("originUrl", originUrl)
            .query(Int::class.java)
            .single() > 0
    }

    override fun findByOriginUrl(originUrl: String): Optional<ShortenedUrl> {
        val sql = """
            SELECT * FROM shortened_url 
            WHERE origin_url = :originUrl
            """.trimIndent()

        return jdbcClient.sql(sql)
            .param("originUrl", originUrl)
            .query { rs, _ -> mapToShortenedUrl(rs) }
            .optional()
    }

    override fun deleteAll() {
        val sql = """
            UPDATE shortened_url 
            SET deleted = TRUE
            WHERE deleted = FALSE
            """.trimIndent()

        jdbcClient.sql(sql)
            .update()
    }

    override fun deleteAllByExpiredAtBefore(baseTime: LocalDateTime) {
        val sql = """
            UPDATE shortened_url 
            SET deleted = TRUE 
            WHERE expired_at < :baseTime
            """.trimIndent()

        jdbcClient.sql(sql)
            .param("baseTime", baseTime)
            .update()
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
