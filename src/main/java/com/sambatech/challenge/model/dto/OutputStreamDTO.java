package com.sambatech.challenge.model.dto;

import java.util.ArrayList;
import java.util.List;

public class OutputStreamDTO {

  private String outputId;
  private String outputPath;
  private List<AclDTO> acl = new ArrayList<>();

  public String getOutputId() {
    return outputId;
  }

  public void setOutputId(String outputId) {
    this.outputId = outputId;
  }

  public String getOutputPath() {
    return outputPath;
  }

  public void setOutputPath(String outputPath) {
    this.outputPath = outputPath;
  }

  public List<AclDTO> getAcl() {
    return acl;
  }

  public void setAcl(List<AclDTO> acl) {
    this.acl = acl;
  }

  public OutputStreamDTO() {
    super();
    this.acl.add(new AclDTO());
  }

  public OutputStreamDTO(String outputId, String outputPath) {
    this.outputId = outputId;
    this.outputPath = outputPath;
    this.acl.add(new AclDTO());
  }

  // TODO: Remove inner class and harcoded  permission setting
  private class AclDTO {
    private String permission = "PUBLIC_READ";

    public String getPermission() {
      return permission;
    }

    public AclDTO() {
      super();
    }
  }
}
