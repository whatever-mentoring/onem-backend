package community.whatever.onembackendkotlin.application

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ShortenUrlIdGeneration {
    companion object {
        private val keyPrefix: String = System.getenv("spring.profiles.active") ?: "local"
    }

    fun generateId(): String {
        val id = UUID.randomUUID().toString()
        return "$keyPrefix-$id"
    }
}
