package community.whatever.onembackendkotlin.application

import community.whatever.onembackendkotlin.application.exception.DomainAlreadyBlockedException
import community.whatever.onembackendkotlin.application.exception.UrlNotFoundException
import community.whatever.onembackendkotlin.domain.ShortenedUrl
import community.whatever.onembackendkotlin.domain.ShortenedUrlRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Transactional(readOnly = true)
@Service
class DefaultUrlShortenService(
    private val shortenedUrlRepository: ShortenedUrlRepository,
    private val blockedDomainService: BlockedDomainService,
    private val idGeneration: ShortenUrlIdGeneration,
) : UrlShortenService {

    override fun getOriginUrl(shortenUrl: String): String {
        return shortenedUrlRepository.findByIdAndDeletedIsFalse(shortenUrl)
            .orElseThrow { UrlNotFoundException() }
            .originUrl
            .also { originUrl ->
                blockedDomainService.isBlocked(originUrl)
                    .takeIf { it }
                    ?.let { throw DomainAlreadyBlockedException() }
            }
    }

    @Transactional
    override fun saveShortenUrl(originUrl: String): ShortenedUrl {
        blockedDomainService.isBlocked(originUrl)
            .takeIf { it }
            ?.let { throw DomainAlreadyBlockedException() }

        return shortenedUrlRepository.findByOriginUrl(originUrl)
            .map { it.takeIf { !it.deleted } ?: it.copy(deleted = false, expiredAt = LocalDateTime.now()) }
            .map { shortenedUrlRepository.save(it) }
            .orElseGet {
                shortenedUrlRepository.save(
                    ShortenedUrl(
                        id = idGeneration.generateId(),
                        originUrl = originUrl,
                        expiredAt = LocalDateTime.now()
                    )
                )
            }
    }
}
