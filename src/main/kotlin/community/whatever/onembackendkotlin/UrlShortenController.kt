package community.whatever.onembackendkotlin

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

@RestController
class UrlShortenController {
    private val shortenUrls = mutableMapOf<String, String>()
    private var key: AtomicLong = AtomicLong(0)

    @PostMapping("/shorten-url/search")
    fun shortenUrlSearch(@RequestBody key: String): String {
        return shortenUrls[key] ?: throw UrlNotFoundException()
    }

    @PostMapping("/shorten-url/create")
    fun shortenUrlCreate(@RequestBody originUrl: String): String {
        shortenUrls.entries.firstOrNull { it.value == originUrl }?.let { return it.key }
        return key.getAndIncrement().toString().also { shortenUrls[it] = originUrl }
    }
}
