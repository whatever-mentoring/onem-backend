package community.whatever.onembackendkotlin.domain

import java.time.LocalDateTime
import java.util.Optional

interface ShortenedUrlRepository {
    /**
     * 아이디를 통해 원본 URL을 찾는다.
     *
     * @param id ShortenedUrl의 id이다.
     * @return 존재하면 ShortenedUrl을 반환하고 존재하지 않으면 null을 반환한다.
     */
    fun findById(id: String): Optional<ShortenedUrl>

    fun findByIdAndDeletedIsFalse(id: String): Optional<ShortenedUrl>

    /**
     * ShortenedUrl을 저장한다.
     *
     * @param shortenedUrl 저장할 ShortenedUrl이다.
     * @return 저장된 ShortenedUrl을 반환한다.
     */
    fun save(shortenedUrl: ShortenedUrl): ShortenedUrl

    /**
     * 원본 URL을 통해 ShortenedUrl을 찾는다.
     *
     * @param originUrl ShortenedUrl의 원본 URL이다.
     * @return 존재하면 ShortenedUrl을 반환하고 존재하지 않으면 null을 반환한다.
     */
    fun existsByOriginUrl(originUrl: String): Boolean

    /**
     * 원본 URL을 통해 ShortenedUrl을 찾는다.
     *
     * @param originUrl ShortenedUrl의 원본 URL이다.
     * @return 존재하면 ShortenedUrl을 반환하고 존재하지 않으면 null을 반환한다.
     */
    fun findByOriginUrl(originUrl: String): Optional<ShortenedUrl>

    /**
     * 모든 ShortenedUrl을 삭제한다.
     */
    fun deleteAll()

    /**
     * 만료일이 주어진 시간 이전인 ShortenedUrl을 모두 삭제한다.
     *
     * @param baseTime 기준 시간이다.
     */
    fun deleteAllByExpiredAtBefore(baseTime: LocalDateTime)
}
