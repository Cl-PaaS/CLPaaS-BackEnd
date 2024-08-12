package com.cloud.cl_paas.domain.validator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

import com.cloud.cl_paas.domain.validator.dto.RespUrlDto;

@Slf4j
@RequiredArgsConstructor
@Service
public class UrlExpanderService {
	public RespUrlDto isUrlShortened(String url) {
		RespUrlDto respUrlDto = new RespUrlDto();
		if (url.length() < 10 || url.contains("bit.ly") || url.contains("goo.gl") || url.contains("tinyurl.com")) {
			String originUrl = expandUrl(url);
			respUrlDto.setOriginalUrl(originUrl);
		} else {
			respUrlDto.setOriginalUrl(url);
		}
		return respUrlDto;
	}

	public String expandUrl(String shortUrl) {
		try {
			URL url = new URL(shortUrl);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setInstanceFollowRedirects(false); // 자동 리다이렉트를 따르지 않도록 설정
			connection.setRequestMethod("HEAD"); // HEAD 요청을 사용하여 응답 헤더만 가져옴
			connection.connect();

			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
				responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
				responseCode == HttpURLConnection.HTTP_SEE_OTHER) {
				return connection.getHeaderField("Location");
			}

			return shortUrl;
		} catch (Exception e) {
			e.printStackTrace();
			return shortUrl;
		}
	}

}
