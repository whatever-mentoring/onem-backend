package community.whatever.onembackendkotlin.application

import community.whatever.onembackendkotlin.application.dto.BlockedDomainCheckRequest
import community.whatever.onembackendkotlin.application.dto.BlockedDomainCreateRequest
import community.whatever.onembackendkotlin.application.dto.BlockedDomainDeleteRequest
import community.whatever.onembackendkotlin.application.exception.DomainAlreadyBlockedException
import community.whatever.onembackendkotlin.application.fack.BlockedDomainInMemoryRepository
import community.whatever.onembackendkotlin.domain.BlockedDomain
import community.whatever.onembackendkotlin.domain.BlockedDomainRepository
import net.datafaker.Faker
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class DefaultBlockedDomainServiceTest {

    private lateinit var blockedDomainService: BlockedDomainService
    private lateinit var blockedDomainRepository: BlockedDomainRepository
    private val faker = Faker()

    @BeforeEach
    fun setUp() {
        blockedDomainRepository = BlockedDomainInMemoryRepository()
        blockedDomainService = DefaultBlockedDomainService(blockedDomainRepository)
    }

    @DisplayName("BlockedDomain 저장")
    @Nested
    inner class SaveBlockedDomain {

        private lateinit var url: String
        private lateinit var blockedDomain: BlockedDomain

        @BeforeEach
        fun setUp() {
            url = faker.internet().url()
            blockedDomain = BlockedDomain(url)
        }

        @Test
        fun `저장된 도메인이 없으면 새로 저장하고 반환한다`() {
            // when
            val result = blockedDomainService.save(
                BlockedDomainCreateRequest(url)
            )

            // then
            assertThat(result.domain).isNotNull
        }

        @Test
        fun `이미 저장된 도메인이 있으면 예외를 발생시킨다`() {
            // given
            val request = BlockedDomainCreateRequest(url)
            blockedDomainService.save(request)

            // when
            assertThatThrownBy { blockedDomainService.save(request) }
                .isInstanceOf(DomainAlreadyBlockedException::class.java)
        }
    }

    @DisplayName("BlockedDomain 삭제")
    @Nested
    inner class DeleteBlockedDomain {

        private lateinit var url: String

        @BeforeEach
        fun setUp() {
            url = faker.internet().url()
        }

        @Test
        fun `저장된 도메인을 삭제한다`() {
            // given
            blockedDomainService.save(BlockedDomainCreateRequest(url))

            // when
            blockedDomainService.delete(BlockedDomainDeleteRequest(url))

            // then
            assertThat(blockedDomainService.isBlocked(BlockedDomainCheckRequest(url))).isFalse()
        }
    }

    @DisplayName("BlockedDomain 조회")
    @Nested
    inner class GetAllBlockedDomains {

        private lateinit var url: String

        @BeforeEach
        fun setUp() {
            url = faker.internet().url()
        }

        @Test
        fun `저장된 모든 도메인을 조회한다`() {
            // given
            blockedDomainService.save(BlockedDomainCreateRequest(url))

            // when
            val result = blockedDomainService.getAll()

            // then
            assertThat(result).isNotEmpty
        }
    }

    @DisplayName("BlockedDomain 조회")
    @Nested
    inner class IsBlockedDomain {

        private lateinit var url: String

        @BeforeEach
        fun setUp() {
            url = faker.internet().url()
        }

        @Test
        fun `저장된 도메인이 존재하는지 확인한다`() {
            // given
            blockedDomainService.save(BlockedDomainCreateRequest(url))

            // when
            val result = blockedDomainService.isBlocked(BlockedDomainCheckRequest(url))

            // then
            assertThat(result).isTrue()
        }

        @Test
        fun `저장된 도메인이 존재하지 않는지 확인한다`() {
            // when
            val result = blockedDomainService.isBlocked(BlockedDomainCheckRequest(url))

            // then
            assertThat(result).isFalse()
        }
    }
}
