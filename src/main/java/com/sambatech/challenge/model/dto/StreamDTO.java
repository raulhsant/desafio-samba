package com.sambatech.challenge.model.dto;

public class StreamDTO {
  private String streamId;

  public String getStreamId() {
    return streamId;
  }

  public void setStreamId(String streamId) {
    this.streamId = streamId;
  }

  public StreamDTO() {
    super();
  }

  public StreamDTO(String streamId) {
    this.streamId = streamId;
  }
}
