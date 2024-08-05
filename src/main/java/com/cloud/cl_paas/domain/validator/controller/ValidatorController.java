package com.cloud.cl_paas.domain.validator.controller;

import com.cloud.cl_paas.domain.parser.dto.ReqMessageDto;
import com.cloud.cl_paas.domain.parser.dto.RespMessageDto;
import com.cloud.cl_paas.domain.parser.service.ParserService;
import com.cloud.cl_paas.domain.validator.service.SafeBrowsingService;
import com.cloud.cl_paas.domain.validator.service.UrlExpanderService;
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
    private final SafeBrowsingService safeBrowsingService;
    private final UrlExpanderService urlExpanderService;

    @PostMapping("/check")
    public String checkUrlSafety(@RequestBody ReqMessageDto reqMessageDto) {
        RespMessageDto parsedData = parserService.getWords(reqMessageDto);

        // 단축 URL의 원본 주소 확인
        String originalUrl = urlExpanderService.expandUrl(parsedData.getUrl());
        log.info("originalUrl = {}", originalUrl);
        // URL의 안전 여부 확인
        String safeStatus = safeBrowsingService.checkUrlSafety(parsedData.getUrl());

            return safeStatus;
    }
}
