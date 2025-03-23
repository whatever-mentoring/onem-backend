package community.whatever.onembackendkotlin.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.net.URL
import java.util.UUID

@Table("BLOCKED_DOMAIN")
data class BlockedDomain(val domain: String, @Id val id: UUID) {
    constructor(url: String) : this(domain = URL(url).host, id = UUID.randomUUID())
}
