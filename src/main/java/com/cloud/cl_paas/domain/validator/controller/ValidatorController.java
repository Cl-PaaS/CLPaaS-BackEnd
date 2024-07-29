package com.cloud.cl_paas.domain.validator.controller;

import com.cloud.cl_paas.domain.parser.dto.ReqMessageDto;
import com.cloud.cl_paas.domain.parser.dto.RespMessageDto;
import com.cloud.cl_paas.domain.parser.service.ParserService;
import com.cloud.cl_paas.domain.validator.service.SafeBrowsingService;
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

    @PostMapping("/check")
    public String checkUrlSafety(@RequestBody ReqMessageDto reqMessageDto) {
        RespMessageDto parsedData = parserService.getWords(reqMessageDto);

        String safeStatus = safeBrowsingService.checkUrlSafety(parsedData.getUrl());
//        String backLinks = safeBrowsingService.getBackLinks(parsedData.getUrl());

            return safeStatus;
    }
}
