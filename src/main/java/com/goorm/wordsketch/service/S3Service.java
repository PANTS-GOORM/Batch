package com.goorm.wordsketch.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    public String uploadImageToS3(String base64Image) {
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imageBytes.length);
        metadata.setContentType("image/jpeg");

        String fileName = UUID.randomUUID() + ".jpg"; // 업로드할 파일명
        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));

        return amazonS3.getUrl(bucketName, fileName).toString();
    }
}
