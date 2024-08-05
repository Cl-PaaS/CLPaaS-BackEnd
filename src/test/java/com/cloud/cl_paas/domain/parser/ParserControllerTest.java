package com.cloud.cl_paas.domain.parser;

import com.cloud.cl_paas.domain.parser.controller.ParserController;
import com.cloud.cl_paas.domain.parser.dto.ReqMessageDto;
import com.cloud.cl_paas.domain.parser.dto.RespMessageDto;
import com.cloud.cl_paas.domain.parser.service.ParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

@WebMvcTest(ParserController.class)
@AutoConfigureRestDocs
public class ParserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParserService parserService;

    private RespMessageDto respMessageDto;

    /* 테스트 수정 필요 */

    @BeforeEach
    public void setup() {
        // Given
        ReqMessageDto reqMessageDto = new ReqMessageDto("Test message with email test@test.com, url http://test.com and phone 123-456-7890");
        respMessageDto = new RespMessageDto("test@test.com", "http://test.com", "123-456-7890");
    }

    @Test
    @DisplayName("정상적인 요청일 때 파싱된 단어를 반환해야 함")
    public void shouldReturnParsedWordsWhenValidRequest() throws Exception {
        // Given
        when(parserService.getWords(any(ReqMessageDto.class))).thenReturn(respMessageDto);

        // When & Then
        mockMvc.perform(get("/parser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"Test message with email test@test.com, url http://test.com and phone 123-456-7890\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("test@test.com")))
                .andExpect(jsonPath("$.url", is("http://test.com")))
                .andExpect(jsonPath("$.phone", is("123-456-7890")))
                .andDo(MockMvcRestDocumentation.document("getWords",
                        PayloadDocumentation.requestFields(
                                fieldWithPath("message").description("The message to be parsed")
                        ),
                        PayloadDocumentation.responseFields(
                                fieldWithPath("email").description("Parsed email from the message"),
                                fieldWithPath("url").description("Parsed URL from the message"),
                                fieldWithPath("phone").description("Parsed phone number from the message")
                        )));
    }

    @Test
    @DisplayName("잘못된 요청일 때 BadRequest를 반환해야 함")
    public void shouldReturnBadRequestWhenInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/parser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invalid\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
    }
}