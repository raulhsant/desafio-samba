package com.sambatech.challenge.model.dto;

public class InputStreamDTO {

  private String inputId;
  private String inputPath;
  private String selectionMode = "AUTO";

  public String getInputId() {
    return inputId;
  }

  public void setInputId(String inputId) {
    this.inputId = inputId;
  }

  public String getInputPath() {
    return inputPath;
  }

  public void setInputPath(String inputPath) {
    this.inputPath = inputPath;
  }

  public String getSelectionMode() {
    return selectionMode;
  }

  public void setSelectionMode(String selectionMode) {
    this.selectionMode = selectionMode;
  }

  public InputStreamDTO() {
    super();
  }

  public InputStreamDTO(String inputId, String inputPath) {
    this.inputId = inputId;
    this.inputPath = inputPath;
  }
}
