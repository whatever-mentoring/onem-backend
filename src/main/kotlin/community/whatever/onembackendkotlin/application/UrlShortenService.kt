package community.whatever.onembackendkotlin.application

import community.whatever.onembackendkotlin.domain.ShortenedUrl

interface UrlShortenService {

    fun getOriginUrl(shortenUrl: String): String
    fun saveShortenUrl(originUrl: String): ShortenedUrl
}
