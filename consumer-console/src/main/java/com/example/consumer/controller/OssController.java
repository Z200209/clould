package com.example.consumer.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.example.common.utils.Response;
import com.example.consumer.domain.OssConfigVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


@RestController()
@RequestMapping("/console/oss")
public class OssController {

    @RequestMapping("/upload")
    public Response uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return new Response(4007);
        }
        if (!file.getContentType().startsWith("image") &&
                !file.getContentType().startsWith("video") &&
                !file.getContentType().startsWith("application")) {
            return new Response(4005);
        }

        // 生成唯一的文件名
        String originalFilename = file.getOriginalFilename(); // 获取原始文件名
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 获取文件扩展名
        String fileName = "";

        // 生成日期路径
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String datePath = sdf.format(new Date());

        // 生成文件名
        String uniqueName = UUID.randomUUID().toString().replace("-", "");

        try {
            if (file.getContentType().startsWith("image")) {
                BufferedImage image = ImageIO.read(file.getInputStream());
                int width = image.getWidth();
                int height = image.getHeight();
                fileName = "image/" + datePath + "/" + uniqueName + "_" + width + "x" + height + fileExtension;
            } else if (file.getContentType().startsWith("video")) {
                fileName = "video/" + datePath + "/" + uniqueName + fileExtension;
            } else if (file.getContentType().startsWith("application")) {
                fileName = "file/" + datePath + "/" + uniqueName + fileExtension;
            }

            OssConfigVO ossConfigVO = new OssConfigVO();
            // 从环境变量中读取敏感信息
            ossConfigVO.setEndpoint(System.getenv("ALIBABA_CLOUD_ENDPOINT"));
            ossConfigVO.setAccessKeyId(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_ID"));
            ossConfigVO.setAccessKeySecret(System.getenv("ALIBABA_CLOUD_ACCESS_KEY_SECRET"));
            ossConfigVO.setBucketName("zzt3");

            // 创建OSSClient实例
            OSS ossClient = null;
            try {
                ossClient = new OSSClientBuilder().build(ossConfigVO.getEndpoint(), ossConfigVO.getAccessKeyId(), ossConfigVO.getAccessKeySecret());
                // 上传文件到OSS
                PutObjectResult result = ossClient.putObject(ossConfigVO.getBucketName(), fileName, file.getInputStream());

                // 返回文件的访问地址
                String fileUrl = "https://" + ossConfigVO.getBucketName() + "." + ossConfigVO.getEndpoint() + "/" + fileName;
                return new Response(1001, fileUrl);
            } finally {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(4008);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(4004);
        }
    }
}