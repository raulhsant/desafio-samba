package com.sambatech.challenge.model.dto.request;

import com.sambatech.challenge.model.dto.InputStreamDTO;

import java.util.ArrayList;
import java.util.List;

public class StreamRequestDTO {

  private String codecConfigId;
  private List<InputStreamDTO> inputStreams = new ArrayList<>();

  public String getCodecConfigId() {
    return codecConfigId;
  }

  public void setCodecConfigId(String codecConfigId) {
    this.codecConfigId = codecConfigId;
  }

  public List<InputStreamDTO> getInputStreams() {
    return inputStreams;
  }

  public void setInputStreams(List<InputStreamDTO> inputStreams) {
    this.inputStreams = inputStreams;
  }

  public StreamRequestDTO() {
    super();
  }
}
