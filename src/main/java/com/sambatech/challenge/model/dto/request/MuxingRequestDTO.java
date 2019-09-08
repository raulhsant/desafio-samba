package com.sambatech.challenge.model.dto.request;

import com.sambatech.challenge.model.dto.OutputStreamDTO;
import com.sambatech.challenge.model.dto.StreamDTO;

import java.util.ArrayList;
import java.util.List;

public class MuxingRequestDTO {

  private Integer segmentLength = 4;
  private String segmentNaming = "seg_%number%.m4s";
  private String initSegmentationName = "init.mp4";
  private List<StreamDTO> streams = new ArrayList<>();
  private List<OutputStreamDTO> outputs = new ArrayList<>();

  public Integer getSegmentLength() {
    return segmentLength;
  }

  public void setSegmentLength(Integer segmentLength) {
    this.segmentLength = segmentLength;
  }

  public String getSegmentNaming() {
    return segmentNaming;
  }

  public void setSegmentNaming(String segmentNaming) {
    this.segmentNaming = segmentNaming;
  }

  public String getInitSegmentationName() {
    return initSegmentationName;
  }

  public void setInitSegmentationName(String initSegmentationName) {
    this.initSegmentationName = initSegmentationName;
  }

  public List<StreamDTO> getStreams() {
    return streams;
  }

  public void setStreams(List<StreamDTO> streams) {
    this.streams = streams;
  }

  public List<OutputStreamDTO> getOutputs() {
    return outputs;
  }

  public void setOutputs(List<OutputStreamDTO> outputs) {
    this.outputs = outputs;
  }

  public MuxingRequestDTO() {
    super();
  }
}
