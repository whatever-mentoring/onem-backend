package community.whatever.onembackendkotlin.application

import community.whatever.onembackendkotlin.application.fack.ShortenedUrlInMemoryRepository
import community.whatever.onembackendkotlin.domain.ShortenedUrl
import community.whatever.onembackendkotlin.domain.ShortenedUrlRepository
import net.datafaker.Faker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ShortenUrlSchedulerTest {

    private lateinit var shortenUrlScheduler: ShortenUrlScheduler
    private lateinit var shortenedUrlRepository: ShortenedUrlRepository
    private val expireMinutes = 1L
    private val faker = Faker()

    @BeforeEach
    fun setUp() {
        shortenedUrlRepository = ShortenedUrlInMemoryRepository()
        shortenUrlScheduler = ShortenUrlScheduler(shortenedUrlRepository, expireMinutes)
    }

    @Nested
    inner class DeleteExpiredShortenedUrl {
        @Test
        fun `발생 시간이 expireMinutes 이상 지난 ShortenedUrl을 삭제한다`() {
            // given
            val expiredOriginUrl = faker.internet().url()
            val expiredShortenedUrl =
                ShortenedUrl(expiredOriginUrl, LocalDateTime.now().minusMinutes(expireMinutes + 1))
            shortenedUrlRepository.save(expiredShortenedUrl)
            val notExpiredOriginUrl = faker.internet().url()
            val notExpiredShortenedUrl =
                ShortenedUrl(notExpiredOriginUrl, LocalDateTime.now().minusMinutes(expireMinutes - 1))
            shortenedUrlRepository.save(notExpiredShortenedUrl)

            // when
            shortenUrlScheduler.deleteExpiredShortenedUrl()

            // then
            Assertions.assertAll(
                { assertThat(shortenedUrlRepository.existsByOriginUrl(expiredOriginUrl)).isFalse },
                { assertThat(shortenedUrlRepository.existsByOriginUrl(notExpiredOriginUrl)).isTrue }
            )
        }
    }
}
