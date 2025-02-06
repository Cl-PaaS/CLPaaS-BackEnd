package com.cloud.cl_paas.domain.validator.service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidateService {
	private static final Set<String> KNOWN_DOMAINS = new HashSet<>();

	static {
		KNOWN_DOMAINS.add("facebook.com");
		KNOWN_DOMAINS.add("google.com");
		KNOWN_DOMAINS.add("naver.com");
		KNOWN_DOMAINS.add("never.com");
		KNOWN_DOMAINS.add("yahoo.com");
	}

	// 키릴 문자 범위: U+0400 ~ U+04FF
	private static final Pattern CYRILLIC_PATTERN = Pattern.compile("[\\u0400-\\u04FF]");

	public void validateEmail(String parsedEmail) {
		// 유사 도메인 검사
		String domain = extractDomain(parsedEmail);
		if (isSimilarToKnownDomain(domain)) {
			throw new IllegalArgumentException("Invalid email: domain is too similar to a known domain");
		}
	}

	public void validateCyrillic(String parsedEmail, String parsedUrl) {
		if (containsCyrillicCharacters(parsedEmail) || containsCyrillicCharacters(parsedUrl)) {
			throw new IllegalArgumentException("Invalid : contains Cyrillic characters");
		}
	}

	// == 편의 메서드 ==
	private boolean containsCyrillicCharacters(String input) {
		return CYRILLIC_PATTERN.matcher(input).find();
	}

	private String extractDomain(String email) {
		return email.substring(email.indexOf('@') + 1);
	}

	private boolean isSimilarToKnownDomain(String domain) {
		for (String knownDomain : KNOWN_DOMAINS) {
			int distance = calculateLevenshteinDistance(domain, knownDomain);
			if (distance <= 1) {
				return true;
			}
		}
		return false;
	}

	private int calculateLevenshteinDistance(String a, String b) {
		int[][] dp = new int[a.length() + 1][b.length() + 1];

		for (int i = 0; i <= a.length(); i++) {
			for (int j = 0; j <= b.length(); j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else {
					dp[i][j] = Math.min(
						dp[i - 1][j - 1] + (a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1),
						Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
					);
				}
			}
		}

		return dp[a.length()][b.length()];
	}

}
