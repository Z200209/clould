package com.example.consumer.domain;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OssConfigVO {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    private String bucketName;

}