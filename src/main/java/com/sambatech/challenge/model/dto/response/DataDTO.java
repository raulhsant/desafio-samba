package com.sambatech.challenge.model.dto.response;

import java.util.Objects;

public class DataDTO {
  private ResultDTO result;

  public DataDTO() {
    super();
  }

  public ResultDTO getResult() {
    return result;
  }

  public void setResult(ResultDTO result) {
    this.result = result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DataDTO dataDTO = (DataDTO) o;
    return Objects.equals(result, dataDTO.result);
  }

  @Override
  public int hashCode() {
    return Objects.hash(result);
  }
}
