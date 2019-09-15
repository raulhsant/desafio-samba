package com.sambatech.challenge.service.bitmovin;

import com.google.gson.JsonParser;
import com.sambatech.challenge.model.UploadedFile;
import com.sambatech.challenge.model.dto.InputStreamDTO;
import com.sambatech.challenge.model.dto.OutputStreamDTO;
import com.sambatech.challenge.model.dto.StreamDTO;
import com.sambatech.challenge.model.dto.request.*;
import com.sambatech.challenge.model.dto.response.GenericResponseDTO;
import com.sambatech.challenge.service.generator.BitmovinAPIServiceGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;

@Service
public class BitmovinService {

  @Value("${bitmovin.apiKey}")
  private String apiKey;

  @Value("${bitmovin.inputId}")
  private String inputId;

  @Value("${bitmovin.outputId}")
  private String outputId;

  @Value("${bitmovin.videoCodecConfigId}")
  private String videoCodecConfigId;

  @Value("${bitmovin.audioCodecConfigId}")
  private String audioCodecConfigId;

  private BitmovinAPIService generateAPIService() {
    if (apiKey == null) {
      return null;
    }

    return BitmovinAPIServiceGenerator.createService(BitmovinAPIService.class, apiKey);
  }

  public String getEncodingStatus(String encodingId) throws Exception {
    BitmovinAPIService apiService = generateAPIService();
    if (apiService == null) {
      throw new Exception("Unable to create BitmovinApiService!");
    }

    GenericResponseDTO response = getStatus(apiService, encodingId);

    return response.getData().getResult().getStatus();
  }

  public void encode(UploadedFile uploadedFile) throws Exception {

    BitmovinAPIService apiService = generateAPIService();
    if (apiService == null) {
      throw new Exception("Unable to create BitmovinApiService!");
    }

    String encodingId =
        createEncoding(apiService, uploadedFile.getUid().toString()).getData().getResult().getId();

    GenericResponseDTO videoStreamResponseDTO =
        createStream(apiService, uploadedFile, encodingId, videoCodecConfigId);
    GenericResponseDTO audioStreamResponseDTO =
        createStream(apiService, uploadedFile, encodingId, audioCodecConfigId);

    GenericResponseDTO videoMuxingResponse =
        createVideoMuxingRequest(apiService, videoStreamResponseDTO, uploadedFile, encodingId);
    GenericResponseDTO audioMuxingResponse =
        createAudioMuxingRequest(apiService, audioStreamResponseDTO, uploadedFile, encodingId);

    GenericResponseDTO manifestResponseDTO = createManifest(apiService, uploadedFile);
    GenericResponseDTO dashPeriodResponseDTO = createDashPeriod(apiService, manifestResponseDTO);
    GenericResponseDTO dashAudioAdaptation = createDashAudioAdaptationSet(apiService, manifestResponseDTO, dashPeriodResponseDTO);
    GenericResponseDTO dashVideoAdaptation = createDashVideoAdaptationSet(apiService, manifestResponseDTO, dashPeriodResponseDTO);

    DashRepresentationRequestDTO videoRepresentationDTO = mountVideoRepresentationDTO(uploadedFile, encodingId, videoMuxingResponse);
    DashRepresentationRequestDTO audioRepresentationDTO = mountAudioRepresentationDTO(uploadedFile, encodingId, audioMuxingResponse);

    createDashRepresentation(apiService, manifestResponseDTO, dashPeriodResponseDTO, dashVideoAdaptation, videoRepresentationDTO);
    createDashRepresentation(apiService, manifestResponseDTO, dashPeriodResponseDTO, dashAudioAdaptation, audioRepresentationDTO);

    GenericResponseDTO encodingStartResponse = startEncoding(apiService, encodingId, manifestResponseDTO);

    uploadedFile.setEncodingId(encodingId);
  }

  private DashRepresentationRequestDTO mountVideoRepresentationDTO(UploadedFile uploadedFile, String encodingId, GenericResponseDTO videoMuxingResponse) {
    DashRepresentationRequestDTO result = new DashRepresentationRequestDTO();
    result.setEncodingId(encodingId);
    result.setMuxingId(videoMuxingResponse.getData().getResult().getId());
    result.setSegmentPath("h264/1024_1500000/fmp4");
    return result;
  }

  private DashRepresentationRequestDTO mountAudioRepresentationDTO(UploadedFile uploadedFile, String encodingId, GenericResponseDTO audioMuxingResponse) {
    DashRepresentationRequestDTO result = new DashRepresentationRequestDTO();
    result.setEncodingId(encodingId);
    result.setMuxingId(audioMuxingResponse.getData().getResult().getId());
    result.setSegmentPath("aac/128000/fmp4");
    return result;
  }

  private void createDashRepresentation(BitmovinAPIService apiService, GenericResponseDTO manifestResponseDTO, GenericResponseDTO periodResponseDTO, GenericResponseDTO adaptation, DashRepresentationRequestDTO representationDTO) throws Exception {
    String manifestId = manifestResponseDTO.getData().getResult().getId();
    String periodId = periodResponseDTO.getData().getResult().getId();
    String adaptationId = adaptation.getData().getResult().getId();

    try {
      Response<GenericResponseDTO> response =
          apiService.createDashRepresentation(manifestId, periodId, adaptationId, representationDTO).execute();

      if (response.isSuccessful()) {
        return;
      }

      throw new Exception(
          String.format(
              "Unable to create Dash Representation for manifest %s, period %s, adaptation %s with code %d | %s",
              manifestId, periodId, adaptationId, response.code(), new String(response.errorBody().bytes())));

    } catch (IOException e) {
      throw new Exception(
          String.format(
              "Unable to create Dash Representation for manifest %s, period %s, adaptation %s with error %s",
              manifestId, periodId, adaptationId, e.getMessage()));
    }
  }

  private GenericResponseDTO createDashVideoAdaptationSet(
      BitmovinAPIService apiService, GenericResponseDTO manifestResponseDTO, GenericResponseDTO periodResponseDTO) throws Exception {
    String manifestId = manifestResponseDTO.getData().getResult().getId();
    String periodId = periodResponseDTO.getData().getResult().getId();
    try {
      Response<GenericResponseDTO> response =
          apiService.createDashVideoAdaptationSet(manifestId, periodId, new JsonParser().parse("{}").getAsJsonObject()).execute();

      if (response.isSuccessful()) {
        return response.body();
      }

      throw new Exception(
          String.format(
              "Unable to create Dash Video Adaptation for manifest %s and period %s with code %d | %s",
              manifestId, periodId, response.code(), new String(response.errorBody().bytes())));

    } catch (IOException e) {
      throw new Exception(
          String.format(
              "Unable to create Dash Video Adaptation for manifest %s and period %s with error %s",
              manifestId, periodId, e.getMessage()));
    }
  }

  private GenericResponseDTO createDashAudioAdaptationSet(
      BitmovinAPIService apiService, GenericResponseDTO manifestResponseDTO, GenericResponseDTO periodResponseDTO) throws Exception {
    String manifestId = manifestResponseDTO.getData().getResult().getId();
    String periodId = periodResponseDTO.getData().getResult().getId();
    try {
      Response<GenericResponseDTO> response =
          apiService
              .createDashAudioAdaptationSet(manifestId, periodId, new JsonParser().parse("{\"lang\": \"en\"}").getAsJsonObject())
              .execute();

      if (response.isSuccessful()) {
        return response.body();
      }

      throw new Exception(
          String.format(
              "Unable to create Dash Audio Adaptation for manifest %s and period %s with code %d | %s",
              manifestId, periodId, response.code(), new String(response.errorBody().bytes())));

    } catch (IOException e) {
      throw new Exception(
          String.format(
              "Unable to create Dash Audio Adaptation for manifest %s and period %s with error %s",
              manifestId, periodId, e.getMessage()));
    }
  }

  private GenericResponseDTO createDashPeriod(BitmovinAPIService apiService, GenericResponseDTO manifestResponseDTO)
      throws Exception {
    String manifestId = manifestResponseDTO.getData().getResult().getId();
    try {
      Response<GenericResponseDTO> response =
          apiService.createDashPeriod(manifestId, new JsonParser().parse("{}").getAsJsonObject()).execute();

      if (response.isSuccessful()) {
        return response.body();
      }

      throw new Exception(
          String.format(
              "Unable to create Dash Period for manifest %s with code %d | %s",
              manifestId, response.code(), new String(response.errorBody().bytes())));

    } catch (IOException e) {
      throw new Exception(
          String.format(
              "Unable to create Dash Period for manifest %s with error %s",
              manifestId, e.getMessage()));
    }
  }

  private GenericResponseDTO createManifest(
      BitmovinAPIService apiService, UploadedFile uploadedFile) throws Exception {
    OutputStreamDTO outputStream = new OutputStreamDTO(outputId, "output/" + uploadedFile.getUid());
    ManifestRequestDTO manifestRequestDTO = new ManifestRequestDTO();
    manifestRequestDTO.setName(uploadedFile.getUid().toString());
    manifestRequestDTO.setManifestName("manifest.mpd"); // uploadedFile.getUid().toString() +
    manifestRequestDTO.getOutputs().add(outputStream);

    try {
      Response<GenericResponseDTO> response =
          apiService.createManifest(manifestRequestDTO).execute();

      if (response.isSuccessful()) {
        return response.body();
      }

      throw new Exception(
          String.format(
              "Unable to create Manifest with code %d | %s",
              response.code(), new String(response.errorBody().bytes())));

    } catch (IOException e) {
      throw new Exception(String.format("Unable to create Manifest with error %s", e.getMessage()));
    }
  }

  private GenericResponseDTO getStatus(BitmovinAPIService apiService,String encodingId) throws Exception {
    try{
      Response<GenericResponseDTO> response = apiService.encodingStatus(encodingId).execute();
      if(response.isSuccessful()){
        return response.body();
      }

      throw new Exception(
          String.format(
              "Unable to check Encoding %s status with code %d | %s",
              encodingId, response.code(), new String(response.errorBody().bytes())));

    } catch (IOException e) {
      throw new Exception(String.format("Unable check Encoding %s status error %s", encodingId, e.getMessage()));
    }
  }

  private GenericResponseDTO createEncoding(BitmovinAPIService apiService, String uid)
      throws Exception {
    EncodingRequestDTO encodingRequestDTO = new EncodingRequestDTO(uid);
    try {
      Response<GenericResponseDTO> response =
          apiService.createEncoding(encodingRequestDTO).execute();

      if (response.isSuccessful()) {
        return response.body();
      }

      throw new Exception(
          String.format(
              "Unable to create Encoding with code %d | %s",
              response.code(), new String(response.errorBody().bytes())));

    } catch (IOException e) {
      throw new Exception(String.format("Unable to create Encoding with error %s", e.getMessage()));
    }
  }

  private GenericResponseDTO createStream(
      BitmovinAPIService apiService,
      UploadedFile uploadedFile,
      String encodingId,
      String codecConfigId)
      throws Exception {
    InputStreamDTO inputStream = new InputStreamDTO(inputId, uploadedFile.getPath().toString());

    StreamRequestDTO streamRequestDTO = new StreamRequestDTO();
    streamRequestDTO.setCodecConfigId(codecConfigId);
    streamRequestDTO.getInputStreams().add(inputStream);

    try {
      Response<GenericResponseDTO> response =
          apiService.createStream(encodingId, streamRequestDTO).execute();

      if (response.isSuccessful()) {
        return response.body();
      }

      throw new Exception(
          String.format(
              "Unable to create Stream for encoding %s with code %d | %s",
              encodingId, response.code(), new String(response.errorBody().bytes())));

    } catch (IOException e) {
      throw new Exception(
          String.format(
              "Unable to create Stream for encoding %s with error %s", encodingId, e.getMessage()));
    }
  }

  private GenericResponseDTO createVideoMuxingRequest(
      BitmovinAPIService apiService,
      GenericResponseDTO videoStreamResponseDTO,
      UploadedFile uploadedFile,
      String encodingId)
      throws Exception {
    StreamDTO stream = new StreamDTO(videoStreamResponseDTO.getData().getResult().getId());
    OutputStreamDTO outputStream =
        new OutputStreamDTO(
            outputId, "output/" + uploadedFile.getUid() + "/h264/1024_1500000/fmp4/");

    MuxingRequestDTO muxingRequestDTO = new MuxingRequestDTO();
    muxingRequestDTO.getStreams().add(stream);
    muxingRequestDTO.getOutputs().add(outputStream);

    try {
      Response<GenericResponseDTO> response =
          apiService.createFMP4Muxing(encodingId, muxingRequestDTO).execute();

      if (response.isSuccessful()) {
        return response.body();
      }

      throw new Exception(
          String.format(
              "Unable to create Muxing for stream %s of enconding %s  with code %d | %s",
              videoStreamResponseDTO.getData().getResult().getId(),
              encodingId,
              response.code(),
              new String(response.errorBody().bytes())));

    } catch (IOException e) {
      throw new Exception(
          String.format(
              "Unable to create Muxing for stream %s of enconding %s with error %s",
              videoStreamResponseDTO.getData().getResult().getId(), encodingId, e.getMessage()));
    }
  }

  private GenericResponseDTO createAudioMuxingRequest(
      BitmovinAPIService apiService,
      GenericResponseDTO audioStreamResponseDTO,
      UploadedFile uploadedFile,
      String encodingId)
      throws Exception {
    StreamDTO stream = new StreamDTO(audioStreamResponseDTO.getData().getResult().getId());
    OutputStreamDTO outputStream =
        new OutputStreamDTO(outputId, "output/" + uploadedFile.getUid() + "/aac/128000/fmp4/");

    MuxingRequestDTO muxingRequestDTO = new MuxingRequestDTO();
    muxingRequestDTO.getStreams().add(stream);
    muxingRequestDTO.getOutputs().add(outputStream);

    try {
      Response<GenericResponseDTO> response =
          apiService.createFMP4Muxing(encodingId, muxingRequestDTO).execute();

      if (response.isSuccessful()) {
        return response.body();
      }

      throw new Exception(
          String.format(
              "Unable to create Muxing for stream %s of enconding %s  with code %d | %s",
              audioStreamResponseDTO.getData().getResult().getId(),
              encodingId,
              response.code(),
              new String(response.errorBody().bytes())));

    } catch (IOException e) {
      throw new Exception(
          String.format(
              "Unable to create Muxing for stream %s of enconding %s with error %s",
              audioStreamResponseDTO.getData().getResult().getId(), encodingId, e.getMessage()));
    }
  }

  private GenericResponseDTO startEncoding(BitmovinAPIService apiService, String encodingId, GenericResponseDTO manifestResponseDTO)
      throws Exception {

    String manifestId = manifestResponseDTO.getData().getResult().getId();
    String requestBody = String.format("{\"vodDashManifests\":[{\"manifestId\":\"%s\"}],\"encodingMode\":\"SINGLE_PASS\"}", manifestId);
    try {
      Response<GenericResponseDTO> response = apiService.startEncoding(encodingId, new JsonParser().parse(requestBody).getAsJsonObject()).execute();

      if (response.isSuccessful()) {
        return response.body();
      }

      throw new Exception(
          String.format(
              "Unable to start encoding %s with code %d | %s",
              encodingId, response.code(), new String(response.errorBody().bytes())));

    } catch (IOException e) {
      throw new Exception(
          String.format("Unable to start encoding %s with error %s", encodingId, e.getMessage()));
    }
  }
}
