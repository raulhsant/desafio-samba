package com.sambatech.challenge.service;

import com.sambatech.challenge.dao.UploadedFileDAO;
import com.sambatech.challenge.model.UploadedFile;
import com.sambatech.challenge.util.MyFileUtil;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UploadedFileService {

  @Autowired UploadedFileDAO uploadedFileDAO;

  public UploadedFile buildUploadFile(MultipartFile multipartFile) throws Exception {
    UUID uid = UUID.randomUUID();

    String multipartFilename = StringUtils.cleanPath(multipartFile.getOriginalFilename());
    String extension = FilenameUtils.getExtension(multipartFilename);
    MyFileUtil.assertFileIsValid(multipartFile, multipartFilename);

    Path filePath = MyFileUtil.mountFilePath(uid.toString() + "." + extension);
    File file = MyFileUtil.convertMultipartToFile(multipartFile, filePath);

    return new UploadedFile.Builder()
        .setUid(uid)
        .setCreatedAt(new Date())
        .setName(multipartFilename)
        .setPath(filePath)
        .setFile(file)
        .build();
  }

  public void save(UploadedFile uploadedFile) {
    uploadedFileDAO.insert(uploadedFile);
  }

  public Optional<UploadedFile> getByUid(String uid){
    return uploadedFileDAO.getByUid(uid);
  }
}
