package community.whatever.onembackendkotlin

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
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
        val originUrls = List(count) { "https://www.google.com/$it" }

        // when
        val keys = originUrls.map { originUrl ->
            async {
                val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
                val request = HttpEntity(originUrl, headers)
                restTemplate.postForEntity("/shorten-url/create", request, String::class.java).body
            }
        }.awaitAll()

        // then
        assertThat(keys).hasSize(count).doesNotHaveDuplicates()
    }
}
