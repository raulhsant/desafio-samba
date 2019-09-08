package com.sambatech.challenge.model.dto.request;

public class EncodingRequestDTO {

  private String name;
  private String description = "Enconding created for the samba technical challenge";
  private String cloudRegion = "AUTO";
  private String encoderVersion = "STABLE";

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCloudRegion() {
    return cloudRegion;
  }

  public void setCloudRegion(String cloudRegion) {
    this.cloudRegion = cloudRegion;
  }

  public String getEncoderVersion() {
    return encoderVersion;
  }

  public void setEncoderVersion(String encoderVersion) {
    this.encoderVersion = encoderVersion;
  }

  public EncodingRequestDTO() {
    super();
  }

  public EncodingRequestDTO(String name) {
    this.name = name;
  }
}
