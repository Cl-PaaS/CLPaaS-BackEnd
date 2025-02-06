package com.cloud.cl_paas.domain.validator.service;

import com.cloud.cl_paas.domain.validator.dto.RespUrlDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UrlExpanderServiceTest {

	@InjectMocks
	private UrlExpanderService urlExpanderService;

	@Mock
	private HttpURLConnection connection;

	@Mock
	private URL url;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("단축 URL이 올바르게 확장된다")
	void isUrlShortened_expandsShortenedUrl() throws Exception {
		// given
		String shortUrl = "https://bit.ly/48hVdQE";
		String expandedUrl = "https://www.bulgari.com/en-us/jewelry/b-zero1?utm_source=Twitter&utm_medium=socialowned&utm_content=post&utm_campaign=bzero12024";
		when(url.openConnection()).thenReturn(connection);
		when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_MOVED_TEMP);
		when(connection.getHeaderField("Location")).thenReturn(expandedUrl);

		// when
		RespUrlDto result = urlExpanderService.isUrlShortened(shortUrl);

		// then
		assertEquals(expandedUrl, result.getOriginalUrl());
	}

	@Test
	@DisplayName("단축되지 않은 URL은 그대로 반환된다")
	void isUrlShortened_returnsNonShortenedUrl() {
		// given
		String nonShortUrl = "http://example.com";

		// when
		RespUrlDto result = urlExpanderService.isUrlShortened(nonShortUrl);

		// then
		assertEquals(nonShortUrl, result.getOriginalUrl());
	}

	@Test
	@DisplayName("URL 확장이 HTTP_MOVED_TEMP를 올바르게 처리한다")
	void expandUrl_handlesHttpMovedTemp() throws Exception {
		// given
		String shortUrl = "https://bit.ly/48hVdQE";
		String expandedUrl = "https://www.bulgari.com/en-us/jewelry/b-zero1?utm_source=Twitter&utm_medium=socialowned&utm_content=post&utm_campaign=bzero12024";
		when(url.openConnection()).thenReturn(connection);
		when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_MOVED_TEMP);
		when(connection.getHeaderField("Location")).thenReturn(expandedUrl);

		// when
		String result = urlExpanderService.expandUrl(shortUrl);

		// then
		assertEquals(expandedUrl, result);
	}

	@Test
	@DisplayName("URL 확장이 리디렉션이 아닌 응답에 대해 원래 URL을 반환한다")
	void expandUrl_returnsOriginalUrlForNonRedirect() throws Exception {
		// given
		String shortUrl = "https://bit.ly/48hVdQE";
		when(url.openConnection()).thenReturn(connection);
		when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

		// when
		String result = urlExpanderService.expandUrl(shortUrl);

		// then
		assertEquals(shortUrl, result);
	}

	@Test
	@DisplayName("URL 확장이 예외를 잘 처리한다")
	void expandUrl_handlesExceptionsGracefully() throws Exception {
		// given
		String shortUrl = "https://bit.ly/48hVdQE";
		when(url.openConnection()).thenThrow(new RuntimeException("Connection error"));

		// when
		String result = urlExpanderService.expandUrl(shortUrl);

		// then
		assertEquals(shortUrl, result);
	}
}