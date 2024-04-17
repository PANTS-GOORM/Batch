package com.goorm.wordsketch.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3Service {

  private final AmazonS3 amazonS3;
  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  public S3Service(AmazonS3 amazonS3) {

    this.amazonS3 = amazonS3;
  }

  /**
   * 전달받은 속담 문제의 이미지를 사전에 정의된 이름으로 S3에 저장하는 함수
   * 
   * @param base64Image
   * @return
   */
  public String uploadProverbImageToS3(String base64Image) {

    byte[] imageBytes = Base64.getDecoder().decode(base64Image);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(imageBytes.length);
    metadata.setContentType("image/jpeg");
    LocalDate currentDate = LocalDate.now();

    // 업로드할 파일명을 {어휘 카테고리}/{배치 실행날짜}/{imageUUID} 로 저장
    String fileName = "proverb/" + currentDate + "/" + UUID.randomUUID() + ".jpg";
    amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));

    return amazonS3.getUrl(bucketName, fileName).toString();
  }
}
