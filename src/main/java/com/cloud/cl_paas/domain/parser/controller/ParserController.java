package com.cloud.cl_paas.domain.parser.controller;

import com.cloud.cl_paas.domain.parser.dto.ReqMessageDto;
import com.cloud.cl_paas.domain.parser.dto.RespMessageDto;
import com.cloud.cl_paas.domain.parser.service.ParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ParserController {

    private final ParserService parserService;

    @GetMapping("/parser")
    public RespMessageDto getWordsFromMessage(@RequestBody ReqMessageDto reqMessageDto) {
        return parserService.getWords(reqMessageDto);
    }
}
