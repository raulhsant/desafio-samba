package com.sambatech.challenge.controller;

import com.sambatech.challenge.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Controller
public class UploadController {

  @Autowired StorageService storageService;

  @Value("${config.allowed-content-types}")
  private Set<String> allowedContentTypes;

  /**
   * This method handles the upload of a video file. As the application only needs to handle .mkv
   * files, the file format if checked in this controller.
   *
   * @param file A multipart or a .mkv file
   * @param response
   * @return A string with the url to access the file.
   */
  @PostMapping(value = "/upload")
  public String handleUpload(@RequestBody MultipartFile file, HttpServletResponse response)
      throws Exception {
    if (file == null) {
      response.sendError(400, "file cannot be null");
      return null;
    }

    // TODO: Send file to aws s3
    if (allowedContentTypes.contains(file.getContentType())) {
      storageService.store(file);
      return "Success!";
    } else {
      response.sendError(
          415, String.format("Content type %s not supported", file.getContentType()));
    }

    return null;
  }
}
