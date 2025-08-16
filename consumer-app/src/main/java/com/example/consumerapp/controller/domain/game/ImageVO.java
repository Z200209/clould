package com.example.consumerapp.controller.domain.game;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ImageVO {
    private String src;
    private Float ar;
}
