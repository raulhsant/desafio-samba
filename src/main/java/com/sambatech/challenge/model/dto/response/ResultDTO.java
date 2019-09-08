package com.sambatech.challenge.model.dto.response;

import java.util.Objects;

public class ResultDTO {

  private String id;
  private String name;

  public ResultDTO() {
    super();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResultDTO resultDTO = (ResultDTO) o;
    return Objects.equals(id, resultDTO.id) &&
        Objects.equals(name, resultDTO.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name);
  }

}
