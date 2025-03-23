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
    private val idGeneration: ShortenUrlIdGeneration,
) : UrlShortenService {

    override fun getOriginUrl(request: ShortenUrlSearchRequest): OriginUrlResponse {
        val id = request.shortenUrl
        return OriginUrlResponse(
            shortenedUrlRepository.findByIdAndDeletedIsFalse(id).orElseThrow { UrlNotFoundException() }.originUrl
                .also { originUrl ->
                    blockedDomainService.isBlocked(BlockedDomainCheckRequest(originUrl))
                        .takeIf { it }
                        ?.let { throw DomainAlreadyBlockedException() }
                }
        )
    }

    @Transactional
    override fun saveShortenUrl(request: ShortenUrlCreateRequest): ShortenedUrlResponse {
        val originUrl = request.originUrl
        blockedDomainService.isBlocked(BlockedDomainCheckRequest(originUrl))
            .takeIf { it }
            ?.let { throw DomainAlreadyBlockedException() }

        return shortenedUrlRepository.findByOriginUrl(originUrl)
            .map { it.copy(deleted = false, expiredAt = LocalDateTime.now()) }
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
            .let { ShortenedUrlResponse(it.id) }
    }
}
