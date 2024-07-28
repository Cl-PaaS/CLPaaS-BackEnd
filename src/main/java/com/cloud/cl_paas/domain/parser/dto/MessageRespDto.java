package com.cloud.cl_paas.domain.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageRespDto {
    private String email;
    private String url;
    private String phone;
}
