package com.sambatech.challenge.controller;

import com.sambatech.challenge.model.UploadedFile;
import com.sambatech.challenge.service.UploadedFileService;
import com.sambatech.challenge.service.bitmovin.BitmovinService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/encoded")
@Api(description = "Set of endpoints for getting encoded info and video")
public class EncodedController {

  @Autowired UploadedFileService uploadedFileService;
  @Autowired BitmovinService bitmovinService;

  @GetMapping("{uploadedUid}/player")
  public String player(@PathVariable String uploadedUid, Model model) throws Exception {

    Optional<UploadedFile> uploadedOptional = uploadedFileService.getByUid(uploadedUid);

    if(uploadedOptional.isEmpty()){
      String errorMessage =  String.format("The video with uid %s cannot be found!", uploadedUid);
      model.addAttribute("errorMessage", errorMessage);
      return "error";
    }

    String encodingStatus = bitmovinService.getEncodingStatus(uploadedOptional.get().getEncodingId());

    if(encodingStatus != null && encodingStatus.equals("FINISHED")){
      model.addAttribute("manifestUrl", "https://s3.sa-east-1.amazonaws.com/io.sambatech.challenge/output/" + uploadedOptional.get().getUid() + "/manifest.mpd");
      return "player";
    }

    String errorMessage =  String.format("The encoding of the video with uid %s isn't finished yet. Please try again in a few minutes.", uploadedUid);
    model.addAttribute("errorMessage", errorMessage);
    return "error";
  }



}
