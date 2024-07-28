package com.cloud.cl_paas.domain.parser.dto;

import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageReqDto {
    private String message;
}
