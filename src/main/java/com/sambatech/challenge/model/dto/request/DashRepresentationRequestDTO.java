package com.sambatech.challenge.model.dto.request;

public class DashRepresentationRequestDTO {

  private String type = "TEMPLATE";
  private String encodingId;
  private String muxingId;
  private String segmentPath;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getEncodingId() {
    return encodingId;
  }

  public void setEncodingId(String encodingId) {
    this.encodingId = encodingId;
  }

  public String getMuxingId() {
    return muxingId;
  }

  public void setMuxingId(String muxingId) {
    this.muxingId = muxingId;
  }

  public String getSegmentPath() {
    return segmentPath;
  }

  public void setSegmentPath(String segmentPath) {
    this.segmentPath = segmentPath;
  }

  public DashRepresentationRequestDTO() {
    super();
  }

  public DashRepresentationRequestDTO(String encodingId, String muxingId, String segmentPath) {
    this.encodingId = encodingId;
    this.muxingId = muxingId;
    this.segmentPath = segmentPath;
  }
}
