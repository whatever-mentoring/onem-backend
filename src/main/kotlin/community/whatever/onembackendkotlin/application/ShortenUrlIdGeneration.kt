package community.whatever.onembackendkotlin.application

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ShortenUrlIdGeneration {
    companion object {
        private val keyPrefix: String =
            System.getenv("SPRING_PROFILES_ACTIVE")
                ?: throw IllegalStateException("SPRING_PROFILES_ACTIVE 시스템 환경변수가 설정되지 않았습니다.")
    }

    fun generateId(): String {
        val id = UUID.randomUUID().toString()
        return "$keyPrefix-$id"
    }
}
