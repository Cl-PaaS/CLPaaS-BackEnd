package com.cloud.cl_paas.domain.parser.service;

import com.cloud.cl_paas.domain.parser.dto.ReqMessageDto;
import com.cloud.cl_paas.domain.parser.dto.RespMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParserService {
    public RespMessageDto getWords(ReqMessageDto reqMessageDto) {
        // 이메일 파싱
        String messages = reqMessageDto.getMessage();

        String email = getEmail(messages);
        String url = getUrl(messages);
        String phone = getPhone(messages);

        return new RespMessageDto(email, url, phone);
    }

    /* 편의 메서드 */
    private String getEmail(String message) {
        Pattern emailPattern = Pattern.compile("([\\p{L}0-9._%+-]+@[\\p{L}0-9.-]+\\.[a-zA-Z]{2,6})");
        Matcher matcher = emailPattern.matcher(message);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private String getUrl(String message) {
        Pattern urlPattern = Pattern.compile("(https?://[\\p{L}0-9\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)");
        Matcher matcher = urlPattern.matcher(message);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private String getPhone(String message) {
        Pattern phonePattern = Pattern.compile("(\\d{2,4}-\\d{3,4}-\\d{4})");
        Matcher matcher = phonePattern.matcher(message);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}
