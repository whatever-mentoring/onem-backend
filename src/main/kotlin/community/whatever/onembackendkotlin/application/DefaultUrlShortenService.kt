package community.whatever.onembackendkotlin.application

import community.whatever.onembackendkotlin.application.dto.BlockedDomainCheckRequest
import community.whatever.onembackendkotlin.application.dto.OriginUrlResponse
import community.whatever.onembackendkotlin.application.dto.ShortenUrlCreateRequest
import community.whatever.onembackendkotlin.application.dto.ShortenUrlSearchRequest
import community.whatever.onembackendkotlin.application.dto.ShortenedUrlResponse
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
) : UrlShortenService {

    override fun getOriginUrl(request: ShortenUrlSearchRequest): OriginUrlResponse {
        val id = request.shortenUrl
        return OriginUrlResponse(
            shortenedUrlRepository.findByIdAndDeletedIsFalse(id)?.originUrl ?: throw UrlNotFoundException()
        )
    }

    @Transactional
    override fun saveShortenUrl(request: ShortenUrlCreateRequest): ShortenedUrlResponse {
        val originUrl = request.originUrl
        if (blockedDomainService.isBlocked(BlockedDomainCheckRequest(originUrl))) {
            throw DomainAlreadyBlockedException()
        }

        shortenedUrlRepository.findByOriginUrl(originUrl)?.let { existingUrl ->
            existingUrl.deleted.takeIf { it }
                ?.run { shortenedUrlRepository.save(existingUrl.copy(deleted = false)) }
                ?: existingUrl
        } ?: shortenedUrlRepository.save(ShortenedUrl(originUrl, LocalDateTime.now()))

        val newUrl = shortenedUrlRepository.save(ShortenedUrl(originUrl, LocalDateTime.now()))
        return newUrl.id?.let { ShortenedUrlResponse(it) } ?: throw UrlNotFoundException()
    }
}
