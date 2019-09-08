package com.sambatech.challenge.dao;

import com.sambatech.challenge.model.UploadedFile;
import org.springframework.stereotype.Repository;

@Repository
public class UploadedFileDAO extends GenericDAO<UploadedFile> {

  public UploadedFileDAO() {
    setClazz(UploadedFile.class);
  }
}
