package com.sambatech.challenge.model.dto.response;

import java.util.Objects;

public class GenericResponseDTO {

  private DataDTO data;

  public GenericResponseDTO() {
    super();
  }

  public DataDTO getData() {
    return data;
  }

  public void setData(DataDTO data) {
    this.data = data;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GenericResponseDTO that = (GenericResponseDTO) o;
    return Objects.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data);
  }
}
