package com.sambatech.challenge.service.bitmovin;

import com.sambatech.challenge.model.UploadedFile;
import com.sambatech.challenge.model.dto.InputStreamDTO;
import com.sambatech.challenge.model.dto.OutputStreamDTO;
import com.sambatech.challenge.model.dto.StreamDTO;
import com.sambatech.challenge.model.dto.request.EncodingRequestDTO;
import com.sambatech.challenge.model.dto.request.MuxingRequestDTO;
import com.sambatech.challenge.model.dto.request.StreamRequestDTO;
import com.sambatech.challenge.model.dto.response.GenericResponseDTO;
import com.sambatech.challenge.service.generator.BitmovinAPIServiceGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.UUID;

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

  private String manifestId = "a19cf927-1184-4cb5-beb3-65c34fce0e41";

  private BitmovinAPIService generateAPIService(){
    if(apiKey == null){
      return null;
    }

    return BitmovinAPIServiceGenerator.createService(BitmovinAPIService.class, apiKey);
  }

  public void encode(UploadedFile uploadedFile) throws Exception {

    BitmovinAPIService apiService = generateAPIService();
    if(apiService == null){
      throw new Exception("Unable to create BitmovinApiService!");
    }

    String encodingId = createEncoding(apiService, uploadedFile.getUid().toString()).getData().getResult().getId();

    GenericResponseDTO streamResponseDTO = createStream(apiService, uploadedFile, encodingId);
    GenericResponseDTO muxingResponse = createMuxingRequest(apiService, streamResponseDTO, uploadedFile, encodingId);

    GenericResponseDTO encodingStartResponse = startEncoding(apiService, encodingId);

    System.out.println(encodingStartResponse);
  }

  private GenericResponseDTO createEncoding(BitmovinAPIService apiService, String uid) throws Exception {
    EncodingRequestDTO encodingRequestDTO = new EncodingRequestDTO(uid);
    try {
      Response<GenericResponseDTO> response = apiService.createEncoding(encodingRequestDTO).execute();

      if(response.isSuccessful()){
        return response.body();
      }

      throw new Exception(String.format("Unable to create Encoding with code %d | %s", response.code(), response.errorBody().toString()));

    } catch (IOException e) {
      throw new Exception(String.format("Unable to create Encoding with error %s", e.getMessage()));
    }
  }

  private GenericResponseDTO createStream(BitmovinAPIService apiService, UploadedFile uploadedFile, String encodingId) throws Exception {
    InputStreamDTO inputStream = new InputStreamDTO(inputId, uploadedFile.getPath().toString());

    StreamRequestDTO streamRequestDTO = new StreamRequestDTO();
    streamRequestDTO.setCodecConfigId(videoCodecConfigId);
    streamRequestDTO.getInputStreams().add(inputStream);

    try {
      Response<GenericResponseDTO> response = apiService.createStream(encodingId, streamRequestDTO).execute();

      if(response.isSuccessful()){
        return response.body();
      }

      throw new Exception(String.format("Unable to create Stream for encoding %s with code %d | %s", encodingId, response.code(), response.errorBody().toString()));

    } catch (IOException e) {
      throw new Exception(String.format("Unable to create Stream for encoding %s with error %s", encodingId, e.getMessage()));
    }
  }

  private GenericResponseDTO createMuxingRequest(BitmovinAPIService apiService, GenericResponseDTO streamResponseDTO, UploadedFile uploadedFile, String encodingId) throws Exception {
    StreamDTO stream = new StreamDTO(streamResponseDTO.getData().getResult().getId());
    OutputStreamDTO outputStream = new OutputStreamDTO(outputId, "output/"+uploadedFile.getUid());

    MuxingRequestDTO  muxingRequestDTO =  new MuxingRequestDTO();
    muxingRequestDTO.getStreams().add(stream);
    muxingRequestDTO.getOutputs().add(outputStream);

    try {
      Response<GenericResponseDTO> response = apiService.createFMP4Muxing(encodingId, muxingRequestDTO).execute();

      if(response.isSuccessful()){
        return response.body();
      }

      throw new Exception(String.format("Unable to create Muxing for stream %s of enconding %s  with code %d | %s", streamResponseDTO.getData().getResult().getId() ,encodingId, response.code(), response.errorBody().toString()));

    } catch (IOException e) {
      throw new Exception(String.format("Unable to create Muxing for stream %s of enconding %s with error %s", streamResponseDTO.getData().getResult().getId() ,encodingId, e.getMessage()));
    }
  }

  private GenericResponseDTO startEncoding(BitmovinAPIService apiService, String encodingId) throws Exception {

    try {
      Response<GenericResponseDTO> response = apiService.startEncoding(encodingId).execute();

      if(response.isSuccessful()){
        return response.body();
      }

      throw new Exception(String.format("Unable to start encoding %s with code %d | %s", encodingId, response.code(), response.errorBody().toString()));

    } catch (IOException e) {
      throw new Exception(String.format("Unable to start encoding %s with error %s", encodingId, e.getMessage()));
    }

  }
}
