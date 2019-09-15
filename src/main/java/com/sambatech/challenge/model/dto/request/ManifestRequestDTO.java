package com.sambatech.challenge.model.dto.request;

import com.sambatech.challenge.model.dto.OutputStreamDTO;

import java.util.ArrayList;
import java.util.List;

public class ManifestRequestDTO {

  private String name;
  private String manifestName;
  private List<OutputStreamDTO> outputs = new ArrayList<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getManifestName() {
    return manifestName;
  }

  public void setManifestName(String manifestName) {
    this.manifestName = manifestName;
  }

  public List<OutputStreamDTO> getOutputs() {
    return outputs;
  }

  public void setOutputs(List<OutputStreamDTO> outputs) {
    this.outputs = outputs;
  }

  public ManifestRequestDTO() {
    super();
  }
}
