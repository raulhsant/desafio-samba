package com.sambatech.challenge.service.generator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BitmovinAPIServiceGenerator {

  private static final String BASE_URL = "https://api.bitmovin.com";

  private static Retrofit.Builder builder =
      new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create());

  private static Retrofit retrofit = builder.build();

  private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

  private static HttpLoggingInterceptor logging =
      new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC);

  public static <S> S createService(Class<S> serviceClass) {
    if (!httpClient.interceptors().contains(logging)) {
      httpClient.addInterceptor(logging);
      builder.client(httpClient.build());
      retrofit = builder.build();
    }
    return retrofit.create(serviceClass);
  }

  public static <S> S createService(Class<S> serviceClass, final String token) {
    if (token != null) {
      httpClient.interceptors().clear();

      httpClient.addInterceptor(
          chain -> {
            Request original = chain.request();
            Request.Builder builder1 = original.newBuilder().header("X-Api-Key", token);
            Request request = builder1.build();
            return chain.proceed(request);
          });

      builder.client(httpClient.build());
      retrofit = builder.build();
    }
    return retrofit.create(serviceClass);
  }
}
