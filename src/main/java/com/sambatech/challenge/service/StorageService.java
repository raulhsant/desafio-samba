package com.sambatech.challenge.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.sambatech.challenge.model.UploadedFile;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);

  @Value("${aws.s3.bucket}")
  private String bucket;

  @Autowired private AmazonS3 amazonS3Client;
  @Autowired private UploadedFileService uploadedFileService;

  public void sendToS3(UploadedFile uploadedFile) throws Exception {

    TransferManager tm =
        TransferManagerBuilder.standard()
            .withS3Client(amazonS3Client)
            .withMultipartUploadThreshold((long) (5 * 1024 * 1025))
            .build();

    PutObjectRequest request =
        new PutObjectRequest(bucket, uploadedFile.getPath().toString(), uploadedFile.getFile());
    Upload upload = tm.upload(request);

    try {
      uploadedFile.setStatus(UploadedFile.STATUS.IN_PROGRESS);
      LOGGER.info("Uploading file {} to S3 bucket!", uploadedFile.getPath().toString());
      upload.waitForCompletion();
      LOGGER.info("Upload {} to S3 bucket completed!", uploadedFile.getPath().toString());
      FileUtils.deleteQuietly(uploadedFile.getFile());
    } catch (AmazonClientException e) {
      uploadedFile.setStatus(UploadedFile.STATUS.FAILED);
      uploadedFileService.save(uploadedFile);
      throw new Exception("Unexpected error while sending data to server " + e);
    }
  }
}
