package community.whatever.onembackendkotlin.presentation

import community.whatever.onembackendkotlin.application.UrlShortenService
import community.whatever.onembackendkotlin.presentation.dto.OriginUrlResponse
import community.whatever.onembackendkotlin.presentation.dto.ShortenUrlCreateRequest
import community.whatever.onembackendkotlin.presentation.dto.ShortenUrlSearchRequest
import community.whatever.onembackendkotlin.presentation.dto.ShortenedUrlResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UrlShortenController(private val urlShortenService: UrlShortenService) {

    @PostMapping("/shorten-url/search")
    fun shortenUrlSearch(@RequestBody request: ShortenUrlSearchRequest): ResponseEntity<OriginUrlResponse> {
        return ResponseEntity.ok(OriginUrlResponse(urlShortenService.getOriginUrl(request.shortenUrl)))
    }

    @PostMapping("/shorten-url/create")
    fun shortenUrlCreate(@RequestBody request: ShortenUrlCreateRequest): ResponseEntity<ShortenedUrlResponse> {
        val saveShortenUrl = urlShortenService.saveShortenUrl(request.originUrl)
        return ResponseEntity.ok(ShortenedUrlResponse(saveShortenUrl.id))
    }
}
