package com.cloud.cl_paas.domain.validator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RespValidateUrlDto {
    private String originalUrl;
    private String safeStatus;
    private String backLinks;
}
