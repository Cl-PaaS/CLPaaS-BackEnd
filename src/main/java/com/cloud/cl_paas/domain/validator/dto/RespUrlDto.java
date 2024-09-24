package com.cloud.cl_paas.domain.validator.dto;

import lombok.Data;

@Data
public class RespUrlDto {
    private Boolean isPhishing;
    private String originalUrl;
}
