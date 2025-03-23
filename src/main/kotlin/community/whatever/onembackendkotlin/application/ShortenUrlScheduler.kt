package community.whatever.onembackendkotlin.application

import community.whatever.onembackendkotlin.domain.ShortenedUrlRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ShortenUrlScheduler(
    private val shortenedUrlRepository: ShortenedUrlRepository,
    @Value("\${shorten-url.expire-minutes}") private val expireMinutes: Long,
) {

    @Transactional
    @Scheduled(fixedRateString = "\${shorten-url.delete-expired-rate}")
    fun deleteExpiredShortenedUrl() {
        shortenedUrlRepository.deleteAllByExpiredAtBefore(LocalDateTime.now().minusMinutes(expireMinutes))
    }
}
