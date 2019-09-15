package com.sambatech.challenge.dao;

import com.sambatech.challenge.model.UploadedFile;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UploadedFileDAO extends GenericDAO<UploadedFile> {

  public UploadedFileDAO() {
    setClazz(UploadedFile.class);
  }

  public Optional<UploadedFile> getByUid(String strUid){
    UUID uid = UUID.fromString(strUid);
    String queryString = "from " + " UploadedFile where uid =: UID";
    TypedQuery<UploadedFile> query = entityManager.createQuery("SELECT uf FROM UploadedFile uf WHERE uf.uid = :UID", UploadedFile.class);
    query.setParameter("UID", uid);

    try{
      return Optional.of(query.getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }
}
