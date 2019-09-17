package com.sambatech.challenge.controller;

import com.sambatech.challenge.model.UploadedFile;
import com.sambatech.challenge.service.StorageService;
import com.sambatech.challenge.service.UploadedFileService;
import com.sambatech.challenge.service.bitmovin.BitmovinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

@Controller
@RequestMapping("/upload")
@Api(description = "Set of endpoints for Uploading a file to be encoded")
public class UploadController {

  @Autowired
  StorageService storageService;
  @Autowired
  UploadedFileService uploadedFileService;
  @Autowired
  BitmovinService bitmovinService;

  @Value("${general.allowed-content-types}")
  private Set<String> allowedContentTypes;

  @Value("${general.hostUri}")
  private String hostUri;

  // TODO: get stream link to play the encoded video

  // TODO: prepare production
  // TODO: implement some tests

  /**
   * This method handles the upload of a video file. As the application only needs to handle .mkv
   * files, the file format if checked in this controller.
   *
   * @param file     A multipart or a .mkv file
   * @param response
   * @return A string with the url to access the file.
   */
  @PostMapping
  @ResponseBody
  @ApiOperation(
      "Start file encoding. Returns information about the encoding and the file uid on the system.")
  public String handleUpload(
      @ApiParam("File to be encoded.") @RequestBody MultipartFile file,
      HttpServletResponse response)
      throws Exception {
    if (file == null) {
      response.sendError(400, "file cannot be found on request");
      return null;
    }

    if (allowedContentTypes.contains(file.getContentType())) {
      UploadedFile uploadedFile = uploadedFileService.buildUploadFile(file);
      storageService.sendToS3(uploadedFile);

      try {
        bitmovinService.encode(uploadedFile);
        uploadedFile.setStatus(UploadedFile.STATUS.SUCCESS);
        uploadedFileService.save(uploadedFile);
      } catch (Exception e) {
        uploadedFile.setStatus(UploadedFile.STATUS.FAILED);
        uploadedFile.setException(e.getMessage());
        uploadedFileService.save(uploadedFile);
        response.sendError(500, e.getMessage());
        e.printStackTrace();
        return null;
      }

      String playerUri = hostUri + "/encoded/" + uploadedFile.getUid() + "/player";

      response.setStatus(201);
      return playerUri;
    }

    response.sendError(415, String.format("Content type %s not supported", file.getContentType()));
    return null;
  }
}
