package community.whatever.onembackendkotlin.infra.repository

import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicLong

@Component
class ShortenUrlIdGeneration {
    companion object {
        private val seq: AtomicLong = AtomicLong()
        private val keyPrefix: String = System.getenv("spring.profiles.active") ?: "local"
    }

    fun generateId(): String {
        val id = seq.incrementAndGet()
        return "$keyPrefix$id"
    }
}
