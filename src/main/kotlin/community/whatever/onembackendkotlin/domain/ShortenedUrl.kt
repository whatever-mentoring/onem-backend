package community.whatever.onembackendkotlin.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("SHORTENED_URL")
data class ShortenedUrl(
    @Id val id: String,
    val originUrl: String,
    val expiredAt: LocalDateTime,
    val deleted: Boolean = false,
)
