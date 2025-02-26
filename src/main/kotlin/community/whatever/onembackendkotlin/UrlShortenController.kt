package community.whatever.onembackendkotlin

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import kotlin.random.Random

@RestController
class UrlShortenController {
    private val shortenUrls = mutableMapOf<String, String>()

    @PostMapping("/shorten-url/search")
    fun shortenUrlSearch(@RequestBody key: String): String {
        return shortenUrls[key] ?: throw IllegalArgumentException("Invalid key")
    }

    @PostMapping("/shorten-url/create")
    fun shortenUrlCreate(@RequestBody originUrl: String): String {
        shortenUrls.forEach { (key, value) ->
            if (value == originUrl) {
                return key
            }
        }
        return Random.nextInt(10000).toString().run {
            shortenUrls[this] = originUrl
            this
        }
    }
}
