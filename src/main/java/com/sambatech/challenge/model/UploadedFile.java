package com.sambatech.challenge.model;

import javax.persistence.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "uploaded_file")
public class UploadedFile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, name = "id", unique = true)
  private Integer id;

  @Column(nullable = false, name = "uid", unique = true)
  private UUID uid;

  @Column(nullable = false, name = "created_at")
  private Date createdAt;

  @Column(nullable = false, name = "name")
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private STATUS status = STATUS.IN_PROGRESS;

  @Column(name = "exception")
  private String exception;

  @Transient private Path path;

  @Transient private File file;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public UUID getUid() {
    return uid;
  }

  public void setUid(UUID uid) {
    this.uid = uid;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Path getPath() {
    return path;
  }

  public void setPath(Path path) {
    this.path = path;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public STATUS getStatus() {
    return status;
  }

  public void setStatus(STATUS status) {
    this.status = status;
  }

  public String getException() {
    return exception;
  }

  public void setException(String exception) {
    this.exception = exception;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UploadedFile that = (UploadedFile) o;
    return Objects.equals(id, that.id)
        && Objects.equals(uid, that.uid)
        && Objects.equals(createdAt, that.createdAt)
        && Objects.equals(name, that.name)
        && Objects.equals(status, that.status);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, uid, createdAt, name, status);
  }

  public UploadedFile() {
    super();
  }

  public UploadedFile(UUID uid, Date createdAt, String name, Path path, File file) {
    this.uid = uid;
    this.createdAt = createdAt;
    this.name = name;
    this.path = path;
    this.file = file;
  }

  public enum STATUS {
    SUCCESS,
    FAILED,
    CANCELLED,
    IN_PROGRESS;
  }

  public static class Builder {

    private UUID uid;
    private Date createdAt;
    private String name;
    private File file;
    private Path path;

    public Builder() {}

    public Builder setUid(UUID uid) {
      this.uid = uid;
      return this;
    }

    public Builder setCreatedAt(Date createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setFile(File file) {
      this.file = file;
      return this;
    }

    public Builder setPath(Path path) {
      this.path = path;
      return this;
    }

    public UploadedFile build() {
      return new UploadedFile(uid, createdAt, name, path, file);
    }
  }
}
