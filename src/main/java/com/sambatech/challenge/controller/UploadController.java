package com.sambatech.challenge.controller;

import com.sambatech.challenge.model.UploadedFile;
import com.sambatech.challenge.service.StorageService;
import com.sambatech.challenge.service.UploadedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Controller
public class UploadController {

  @Autowired StorageService storageService;

  @Autowired UploadedFileService uploadedFileService;

  @Value("${general.allowed-content-types}")
  private Set<String> allowedContentTypes;

  // TODO: Create stream
  // TODO: Create muxing
  // TODO: encode

  /**
   * This method handles the upload of a video file. As the application only needs to handle .mkv
   * files, the file format if checked in this controller.
   *
   * @param file A multipart or a .mkv file
   * @param response
   * @return A string with the url to access the file.
   */
  @PostMapping(value = "/upload")
  @ResponseBody
  public String handleUpload(@RequestBody MultipartFile file, HttpServletResponse response)
      throws Exception {
    if (file == null) {
      response.sendError(400, "file cannot be found on request");
      return null;
    }

    if (allowedContentTypes.contains(file.getContentType())) {
      UploadedFile uploadedFile = uploadedFileService.buildUploadFile(file);
      storageService.sendToS3(uploadedFile);
      uploadedFileService.save(uploadedFile);
      response.setStatus(201);
      return "Success!";
    }

    response.sendError(415, String.format("Content type %s not supported", file.getContentType()));
    return null;
  }
}