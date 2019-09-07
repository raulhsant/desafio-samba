package com.sambatech.challenge.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);

  private final Path rootLocation = Paths.get("upload-dir");

  @Value("${aws.s3.bucket}")
  private String bucket;

  @Autowired private AmazonS3 amazonS3Client;

  public void store(MultipartFile file) throws Exception {
    String filename = StringUtils.cleanPath(file.getOriginalFilename());
    assertFileIsValid(file, filename);
    try (InputStream inputStream = file.getInputStream()) {
      Files.copy(
          inputStream, this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new Exception("Failed to store file " + filename, e);
    }
  }

  private void assertFileIsValid(MultipartFile file, String filename) throws Exception {
    if (file.isEmpty()) {
      throw new Exception("Failed to store empty file " + filename);
    }
    if (filename.contains("..")) {
      // This is a security check
      throw new Exception(
          "Cannot store file with relative path outside current directory " + filename);
    }
  }

  public void sendToS3(MultipartFile multipartFile) throws Exception {
    String multipartFilename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
    assertFileIsValid(multipartFile, multipartFilename);
    String filePath = mountFilePath(multipartFilename);
    File file = convertMultipartToFile(multipartFile, filePath);

    TransferManager tm =
        TransferManagerBuilder.standard()
            .withS3Client(amazonS3Client)
            .withMultipartUploadThreshold((long) (5 * 1024 * 1025))
            .build();

    PutObjectRequest request = new PutObjectRequest(bucket, filePath, file);
    Upload upload = tm.upload(request);

    try {
      LOGGER.info("Uploading file {} to S3 bucket!", filePath);
      upload.waitForCompletion();
      LOGGER.info("Upload {} to S3 bucket completed!", filePath);
    } catch (AmazonClientException e) {
      e.printStackTrace();
      throw new Exception("Unexpected error while sending data to server " + e);
    }
  }

  private String mountFilePath(String filename) {
    return this.rootLocation.resolve(filename).toString();
  }

  private File convertMultipartToFile(MultipartFile multipartFile, String path) throws IOException {
    File file = new File(path);
    file.createNewFile();
    FileOutputStream fos = new FileOutputStream(file);
    fos.write(multipartFile.getBytes());
    fos.close();
    return file;
  }
}
