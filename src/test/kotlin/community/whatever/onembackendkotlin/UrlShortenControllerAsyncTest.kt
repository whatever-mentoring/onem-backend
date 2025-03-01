package community.whatever.onembackendkotlin

import kotlinx.coroutines.async
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UrlShortenControllerAsyncTest {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @ParameterizedTest
    @CsvSource("50", "500", "10000")
    fun `동시에 여러 개의 원본 URL을 등록해도 키가 중복되지 않는다`(count: Int) = runTest {
        // given
        val originUrls = (1..count).map { "https://www.google.com/$it" }

        // when
        val keys = originUrls.map { async { createUrl(it) } }

        // then
        await untilAsserted { assertThat(keys).hasSize(count).doesNotHaveDuplicates() }
    }

    private fun createUrl(originUrl: String): String? {
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val request = HttpEntity(originUrl, headers)
        return restTemplate.postForEntity("/shorten-url/create", request, String::class.java).body
    }
}
