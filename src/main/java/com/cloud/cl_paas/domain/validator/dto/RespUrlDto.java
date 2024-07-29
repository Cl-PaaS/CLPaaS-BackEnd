package com.cloud.cl_paas.domain.validator.dto;

import lombok.Data;

@Data
public class RespUrlDto {
    private Boolean isValid;
    private String validateMessage;
}
