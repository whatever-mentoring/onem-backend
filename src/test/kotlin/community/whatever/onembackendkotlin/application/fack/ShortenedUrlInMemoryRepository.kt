package community.whatever.onembackendkotlin.application.fack

import community.whatever.onembackendkotlin.application.ShortenUrlIdGeneration
import community.whatever.onembackendkotlin.domain.ShortenedUrl
import community.whatever.onembackendkotlin.domain.ShortenedUrlRepository
import java.time.LocalDateTime
import java.util.Optional

class ShortenedUrlInMemoryRepository : ShortenedUrlRepository {

    private val shortenUrls = mutableMapOf<String, ShortenedUrl>()

    override fun findById(id: String): Optional<ShortenedUrl> {
        return Optional.ofNullable(shortenUrls[id])
    }

    override fun findByIdAndDeletedIsFalse(id: String): Optional<ShortenedUrl> {
        return Optional.ofNullable(shortenUrls[id]?.takeUnless { it.deleted })
    }

    override fun save(shortenedUrl: ShortenedUrl): ShortenedUrl {
        if (shortenedUrl.id.isNotBlank() && shortenUrls.containsKey(shortenedUrl.id)) {
            shortenUrls[shortenedUrl.id] = shortenedUrl
            return shortenedUrl
        }
        val id = ShortenUrlIdGeneration().generateId()
        return shortenedUrl.copy(id = id).also { shortenUrls[id] = it }
    }

    override fun existsByOriginUrl(originUrl: String): Boolean {
        return shortenUrls.values.any { it.originUrl == originUrl && !it.deleted }
    }

    override fun findByOriginUrl(originUrl: String): Optional<ShortenedUrl> {
        return shortenUrls.values.find { it.originUrl == originUrl }?.let { Optional.of(it) } ?: Optional.empty()
    }

    override fun deleteAll() {
        shortenUrls.clear()
    }

    override fun deleteAllByExpiredAtBefore(baseTime: LocalDateTime) {
        shortenUrls.replaceAll { _, url ->
            url.takeUnless { it.expiredAt.isBefore(baseTime) } ?: url.copy(deleted = true)
        }
    }
}
