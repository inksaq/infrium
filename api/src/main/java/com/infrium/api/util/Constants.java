package com.infrium.api.util;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import com.infrium.api.mongoserializer.annotation.Exclude;

import java.net.http.HttpClient;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Constants {

  private static final Constants constants = new Constants();

  public final int REDIS_DB_CLOUD = 1;

  @Getter private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(30);
  @Getter private final HttpClient httpClient = HttpClient.newBuilder().executor(executor).build();

  private final ExclusionStrategy gsonStrategy =
      new ExclusionStrategy() {
        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
          return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes field) {
          return field.getAnnotation(Exclude.class) != null;
        }
      };

  @Getter
  private final Gson gson =
      new GsonBuilder()
          .addDeserializationExclusionStrategy(gsonStrategy)
          .addSerializationExclusionStrategy(gsonStrategy)
          .create();

  private Constants() {}

  public static Constants get() {
    return constants;
  }

  public final void shutdown() {
    executor.shutdown();
  }
}
