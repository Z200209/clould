package com.example.common.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class IntroductionDTO {
    private String type;
    private String content;
}
