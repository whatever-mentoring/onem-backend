package community.whatever.onembackendkotlin.application.fack

import community.whatever.onembackendkotlin.domain.ShortenedUrl
import community.whatever.onembackendkotlin.domain.ShortenedUrlRepository
import community.whatever.onembackendkotlin.infra.repository.ShortenUrlIdGeneration
import java.time.LocalDateTime

class ShortenedUrlInMemoryRepository : ShortenedUrlRepository {

    private val shortenUrls = mutableMapOf<String, ShortenedUrl>()

    override fun findById(id: String): ShortenedUrl? {
        return shortenUrls[id]
    }

    override fun findByIdAndDeletedIsFalse(id: String): ShortenedUrl? {
        return shortenUrls[id]?.takeUnless { it.deleted }
    }

    override fun save(shortenedUrl: ShortenedUrl): ShortenedUrl {
        val id = ShortenUrlIdGeneration().generateId()
        return shortenedUrl.copy(id = id).also { shortenUrls[id] = it }
    }

    override fun existsByOriginUrl(originUrl: String): Boolean {
        return shortenUrls.values.any { it.originUrl == originUrl && !it.deleted }
    }

    override fun findByOriginUrl(originUrl: String): ShortenedUrl? {
        return shortenUrls.values.find { it.originUrl == originUrl }
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
