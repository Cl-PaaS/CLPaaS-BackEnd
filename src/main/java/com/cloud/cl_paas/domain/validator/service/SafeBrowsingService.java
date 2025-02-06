package com.cloud.cl_paas.domain.validator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class SafeBrowsingService {

    @Value("${google.safebrowsing.api.key}")
    private String googleApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String checkUrlSafety(String url) {
        if (url.isEmpty()) {
            return "No URL provided";
        }

        String safeBrowsingApiUrl = UriComponentsBuilder
                .fromHttpUrl("https://safebrowsing.googleapis.com/v4/threatMatches:find")
                .queryParam("key", googleApiKey)
                .toUriString();

        Map<String, Object> requestBody = createRequestBody(url);

        String response = restTemplate.postForObject(safeBrowsingApiUrl, requestBody, String.class);

        if (response != null && !response.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String, Object> jsonResponse = objectMapper.readValue(response, new TypeReference<>() {
                });
                return jsonResponse.get("matches") != null ? "unsafe" : "safe";
            } catch (JsonProcessingException e)  { // TODO: 예외 처리 방법 변경 예정
                return "Error processing the response";
            }
        }

        return "No response";
    }

    private Map<String, Object> createRequestBody(String url) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("client", createClientObject());
        requestBody.put("threatInfo", createThreatInfoObject(url));
        return requestBody;
    }

    private Map<String, String> createClientObject() {
        Map<String, String> client = new HashMap<>();
        client.put("clientId", "cl-paas");
        client.put("clientVersion", "1.5.2");
        return client;
    }

    private Map<String, Object> createThreatInfoObject(String url) {
        Map<String, Object> threatInfo = new HashMap<>();
        threatInfo.put("threatTypes", Arrays.asList("MALWARE", "SOCIAL_ENGINEERING"));
        threatInfo.put("platformTypes", Collections.singletonList("ANY_PLATFORM"));
        threatInfo.put("threatEntryTypes", Collections.singletonList("URL"));
        Map<String, String> threatEntries = new HashMap<>();
        threatEntries.put("url", url);
        threatInfo.put("threatEntries", Collections.singletonList(threatEntries));
        return threatInfo;
    }
}
