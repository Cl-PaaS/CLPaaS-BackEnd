package com.cloud.cl_paas.domain.validator.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class PhishtankValidateServiceTest {

	@InjectMocks
	private PhishtankValidateService phishtankValidateService;

	@Mock
	private HttpURLConnection connection;

	@Mock
	private URL url;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		when(url.openConnection()).thenReturn(connection);
	}

	@Test
	@DisplayName("URL이 안전한 경우 'safe'를 반환한다.")
	void checkUrlSafety_returnsSafe_whenUrlIsSafe() throws Exception {
		// given
		when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
		InputStream inputStream = new ByteArrayInputStream(
			("<response><in_database>true</in_database><valid>false</valid></response>").getBytes()
		);
		when(connection.getInputStream()).thenReturn(inputStream);

		// when
		String result = phishtankValidateService.checkUrlSafety("https://www.naver.com/");

		// then
		assertEquals("safe", result);
	}

	@Test
	@DisplayName("URL이 안전하지 않은 경우 'unsafe'를 반환한다.")
	void checkUrlSafety_returnsUnsafe_whenUrlIsUnsafe() throws Exception {
		// given
		when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
		InputStream inputStream = new ByteArrayInputStream(
			("<response><in_database>true</in_database><valid>true</valid></response>").getBytes()
		);
		when(connection.getInputStream()).thenReturn(inputStream);

		// when
		String result = phishtankValidateService.checkUrlSafety("https://globalhftyh.com");

		// then
		assertEquals("unsafe", result);
	}

	@Test
	@DisplayName("응답이 OK가 아닌 경우 'unknown'을 반환한다.")
	void checkUrlSafety_returnsUnknown_whenResponseIsNotOk() throws Exception {
		// given
		when(connection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);

		// when
		String result = phishtankValidateService.checkUrlSafety("http://unknown-url.com");

		// then
		assertEquals("unknown", result);
	}

	@Test
	@DisplayName("예외가 발생한 경우 'unknown'을 반환한다.")
	void checkUrlSafety_returnsUnknown_whenExceptionOccurs() throws Exception {
		// given
		when(connection.getResponseCode()).thenThrow(new RuntimeException("Connection error"));

		// when
		String result = phishtankValidateService.checkUrlSafety("http://error-url.com");

		// then
		assertEquals("unknown", result);
	}
}