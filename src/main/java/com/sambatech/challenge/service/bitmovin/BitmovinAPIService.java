package com.sambatech.challenge.service.bitmovin;

import com.google.gson.JsonObject;
import com.sambatech.challenge.model.dto.request.*;
import com.sambatech.challenge.model.dto.response.GenericResponseDTO;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.Map;

public interface BitmovinAPIService {

  @POST("/v1/encoding/encodings")
  Call<GenericResponseDTO> createEncoding(@Body EncodingRequestDTO encodingRequestDTO);

  @POST("/v1/encoding/manifests/dash")
  Call<GenericResponseDTO> createManifest(@Body ManifestRequestDTO manifestRequestDTO);

  @POST("/v1/encoding/encodings/{id}/streams")
  Call<GenericResponseDTO> createStream(
      @Path("id") String encodingId, @Body StreamRequestDTO streamRequestDTO);

  @POST("/v1/encoding/encodings/{id}/muxings/fmp4")
  Call<GenericResponseDTO> createFMP4Muxing(
      @Path("id") String encodingId, @Body MuxingRequestDTO muxingRequestDTO);

  @POST("/v1/encoding/encodings/{id}/start")
  Call<GenericResponseDTO> startEncoding(@Path("id") String encodingId, @Body JsonObject body);

  @POST("/v1/encoding/manifests/dash/{id}/periods")
  Call<GenericResponseDTO> createDashPeriod(@Path("id") String manifestId, @Body JsonObject body);

  @POST("/v1/encoding/manifests/dash/{manifest_id}/periods/{period_id}/adaptationsets/audio")
  Call<GenericResponseDTO> createDashAudioAdaptationSet(
      @Path("manifest_id") String manifestId,
      @Path("period_id") String periodId,
      @Body JsonObject body);

  @POST("/v1/encoding/manifests/dash/{manifest_id}/periods/{period_id}/adaptationsets/video")
  Call<GenericResponseDTO> createDashVideoAdaptationSet(
      @Path("manifest_id") String manifestId,
      @Path("period_id") String periodId,
      @Body JsonObject body);

  @POST(
      "/v1/encoding/manifests/dash/{manifest_id}/periods/{period_id}/adaptationsets/{adaptationset_id}/representations/fmp4")
  Call<GenericResponseDTO> createDashRepresentation(
      @Path("manifest_id") String manifestId,
      @Path("period_id") String periodId,
      @Path("adaptationset_id") String adaptationSetId,
      @Body DashRepresentationRequestDTO dashRepresentationRequestDTO);

  @GET("/v1/encoding/encodings/{id}/status")
  Call<GenericResponseDTO> encodingStatus(@Path("id") String encodingId);
}
