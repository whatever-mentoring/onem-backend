package community.whatever.onembackendkotlin.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("SHORTENED_URL")
data class ShortenedUrl(
    val originUrl: String,
    val expiredAt: LocalDateTime,
    @Id val id: String? = null,
    val deleted: Boolean = false,
)
