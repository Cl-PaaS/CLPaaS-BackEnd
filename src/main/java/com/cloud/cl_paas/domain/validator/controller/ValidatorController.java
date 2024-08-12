package com.cloud.cl_paas.domain.validator.controller;

import com.cloud.cl_paas.domain.parser.dto.ReqMessageDto;
import com.cloud.cl_paas.domain.parser.dto.RespMessageDto;
import com.cloud.cl_paas.domain.parser.service.ParserService;
import com.cloud.cl_paas.domain.validator.dto.RespUrlDto;
import com.cloud.cl_paas.domain.validator.service.PhishtankValidateService;
import com.cloud.cl_paas.domain.validator.service.SafeBrowsingService;
import com.cloud.cl_paas.domain.validator.service.UrlExpanderService;
import com.cloud.cl_paas.domain.validator.service.ValidateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ValidatorController {

	private final ParserService parserService;
	private final ValidateService validateService;
	private final SafeBrowsingService safeBrowsingService;
	private final PhishtankValidateService phishtankValidateService;
	private final UrlExpanderService urlExpanderService;

	@PostMapping("/check")
	public RespUrlDto checkUrlSafety(@RequestBody ReqMessageDto reqMessageDto) {
		RespMessageDto parsedData = parserService.getWords(reqMessageDto);
		log.info("parsedData = {}", parsedData);

		// 키릴 문자 확인
		validateService.validateCyrillic(parsedData.getEmail(), parsedData.getUrl());

		// 변조 의심 이메일 확인
		validateService.validateEmail(parsedData.getEmail());

		// 단축 URL의 원본 주소 확인
		RespUrlDto respUrlDto = urlExpanderService.isUrlShortened(parsedData.getUrl());
		log.info("originalUrl = {}", respUrlDto.getOriginalUrl());

		// URL의 안전 여부 확인
		String GoogleSafeStatus = safeBrowsingService.checkUrlSafety(parsedData.getUrl());
		log.info("GoogleSafeStatus = {}", GoogleSafeStatus);

		// 피시 탱크를 통한 안전 여부 확인
		String phishtankSafeStatus = phishtankValidateService.checkUrlSafety(parsedData.getUrl());
		log.info("phishtankSafeStatus = {}", phishtankSafeStatus);

		if (GoogleSafeStatus.equals("safe") && phishtankSafeStatus.equals("safe")) {
			respUrlDto.setSafeStatus("safe");
		} else {
			respUrlDto.setSafeStatus("danger");
		}

		return respUrlDto;
	}
}
