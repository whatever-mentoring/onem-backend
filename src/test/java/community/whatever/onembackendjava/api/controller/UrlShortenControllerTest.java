package community.whatever.onembackendjava.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import community.whatever.onembackendjava.api.dto.ShortenUrlDto;
import community.whatever.onembackendjava.setting.RestDocsTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UrlShortenControllerTest  extends RestDocsTestSupport {

    @Test
    void shortenUrlSearch() throws Exception{
        ShortenUrlDto.Get.Request dto = new ShortenUrlDto.Get.Request();
        dto.setKey("11111111");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        Map<String, String> map = objectMapper.convertValue(dto, new TypeReference<Map<String, String>>() {
        });
        params.setAll(map);

        ResultActions result = this.mockMvc.perform(
                get("/shorten-url")
                        .contentType(contentType)
                        .accept(contentType)
                        .params(params)
        );

        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("key").description("short url key")
                                ),
                                responseFields(
                                        fieldWithPath("resultCode").type(JsonFieldType.STRING).description("결과 코드"),
                                        fieldWithPath("msg").type(JsonFieldType.STRING).description("결과 메시지"),
                                        fieldWithPath("timeZone").type(JsonFieldType.STRING).description("서버 타임존"),
                                        fieldWithPath("timeStamp").type(JsonFieldType.NUMBER).description("서버 응답시간"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 객체").optional(),
                                        fieldWithPath("data.originUrl").type(JsonFieldType.STRING).description("원본 url").optional()
                                )
                        )
                );
    }

    @Test
    void shortenUrlCreate() throws Exception {
        ShortenUrlDto.Create.Request dto  = new ShortenUrlDto.Create.Request();
        dto.setOriginUrl("https://www.google.com");

        ResultActions result = this.mockMvc.perform(
                post("/shorten-url")
                        .contentType(contentType)
                        .accept(contentType)
                        .content(objectMapper.writeValueAsString(dto))
        );

        result.andExpect(status().isOk())
                .andDo(print())
                .andDo(restDocs.document(
                                requestFields(
                                        fieldWithPath("originUrl").type(JsonFieldType.STRING).description("요청 url")
                                ),
                                responseFields(
                                        fieldWithPath("resultCode").type(JsonFieldType.STRING).description("결과 코드"),
                                        fieldWithPath("msg").type(JsonFieldType.STRING).description("결과 메시지"),
                                        fieldWithPath("timeZone").type(JsonFieldType.STRING).description("서버 타임존"),
                                        fieldWithPath("timeStamp").type(JsonFieldType.NUMBER).description("서버 응답시간"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과 객체").optional(),
                                        fieldWithPath("data.key").type(JsonFieldType.STRING).description("생성된 키").optional()
                                )
                        )
                );
    }
}