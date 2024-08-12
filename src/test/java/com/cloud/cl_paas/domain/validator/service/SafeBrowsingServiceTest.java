package com.cloud.cl_paas.domain.validator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SafeBrowsingServiceTest {

	@InjectMocks
	private SafeBrowsingService safeBrowsingService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private ObjectMapper objectMapper;

	@Value("${google.safebrowsing.api.key}")
	private String googleApiKey;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(safeBrowsingService, "googleApiKey", "AIzaSyCu_eGpcQESztKLOgvfIXgA2VHd9LFlyJM");
	}

	// 구글 세이프 브라우징에서 걸리는 URL을 찾기가 어려워 safe만 테스트

	@Test
	@DisplayName("URL이 안전한 경우 'safe'를 반환한다.")
	void checkUrlSafety_returnsSafe_whenUrlIsSafe() throws Exception {
		// given
		String url = "https://naver.com";
		String response = "{}";
		when(restTemplate.postForObject(any(String.class), any(Map.class), eq(String.class))).thenReturn(response);
		when(objectMapper.readValue(response, new TypeReference<Map<String, Object>>() {
		})).thenReturn(new HashMap<>());

		// when
		String result = safeBrowsingService.checkUrlSafety(url);

		// then
		assertEquals("safe", result);
	}
}