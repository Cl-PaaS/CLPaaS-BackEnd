package com.cloud.cl_paas.domain.validator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@RequiredArgsConstructor
@Service
public class PhishtankValidateService {

    private static final String API_URL = "https://checkurl.phishtank.com/checkurl/";
    private static final String USER_AGENT = "phishtank/hideaki";

    public String checkUrlSafety(String url) {
        try {
            String fullUrl = API_URL + "?url=" + url + "&format=json";


            URL obj = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            int status = connection.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document doc = builder.parse(new org.xml.sax.InputSource(new java.io.StringReader(response.toString())));

                    doc.getDocumentElement().normalize();

                    NodeList inDatabaseList = doc.getElementsByTagName("in_database");
                    NodeList validList = doc.getElementsByTagName("valid");

                    if (inDatabaseList.getLength() > 0 && validList.getLength() > 0) {
                        String inDatabase = inDatabaseList.item(0).getTextContent();
                        String valid = validList.item(0).getTextContent();

                        if ("true".equalsIgnoreCase(inDatabase) && "true".equalsIgnoreCase(valid)) {
                            return "unsafe";
                        } else {
                            return "safe";
                        }
                    }
                }
            } else {
                log.warn("Received non-OK response: {} {}", status, connection.getResponseMessage());
                return "unknown";
            }
        } catch (Exception e) {
            log.error("Error checking URL safety", e);
            return "unknown";
        }
        return "unknown";
    }
}
