package com.sambatech.challenge.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MyFileUtil {

  private static final String ROOT_LOCATION_STRING = "upload";

  public static File convertMultipartToFile(MultipartFile multipartFile, Path path)
      throws IOException {
    File file = path.toFile();
    file.createNewFile();
    FileOutputStream fos = new FileOutputStream(file);
    fos.write(multipartFile.getBytes());
    fos.close();
    return file;
  }

  public static void assertFileIsValid(MultipartFile file, String filename) throws Exception {
    if (file.isEmpty()) {
      throw new Exception("Failed to store empty file " + filename);
    }
    if (filename.contains("..")) {
      // This is a security check
      throw new Exception(
          "Cannot store file with relative path outside current directory " + filename);
    }
  }

  public static Path mountFilePath(String filename) {
    Path rootLocation = Paths.get(MyFileUtil.ROOT_LOCATION_STRING);
    return rootLocation.resolve(filename);
  }
}
