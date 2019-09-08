package com.sambatech.challenge.service.bitmovin;

import com.sambatech.challenge.model.dto.request.EncodingRequestDTO;
import com.sambatech.challenge.model.dto.request.ManifestRequestDTO;
import com.sambatech.challenge.model.dto.request.MuxingRequestDTO;
import com.sambatech.challenge.model.dto.request.StreamRequestDTO;
import com.sambatech.challenge.model.dto.response.GenericResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BitmovinAPIService {

  @POST("/v1/encoding/encodings")
  Call<GenericResponseDTO> createEncoding(@Body EncodingRequestDTO encodingRequestDTO);

  @POST("/v1/encoding/manifests/dash")
  Call<GenericResponseDTO> createManifest(@Body ManifestRequestDTO manifestRequestDTO);

  @POST("/v1/encoding/encodings/{id}/streams")
  Call<GenericResponseDTO> createStream(@Path("id") String encodingId, @Body StreamRequestDTO streamRequestDTO);

  @POST("/v1/encoding/encodings/{id}/muxings/fmp4")
  Call<GenericResponseDTO> createFMP4Muxing(@Path("id") String encodingId, @Body MuxingRequestDTO muxingRequestDTO);

  @POST("/v1/encoding/encodings/{id}/start")
  Call<GenericResponseDTO> startEncoding(@Path("id") String encodingId);

  @GET("/v1/encoding/encodings/{id}/status")
  Call<GenericResponseDTO> encodingStatus(@Path("id") String encodingId);

}
