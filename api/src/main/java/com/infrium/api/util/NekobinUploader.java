package com.infrium.api.util;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class NekobinUploader {

  private static URI uri = null;

  NekobinUploader() {}

  @SneakyThrows
  public static CompletableFuture<NekobinResult> upload(@NonNull String content) {

    if (uri == null) uri = new URI("https://nekobin.com/api/documents");

    var o = new JsonObject();
    o.addProperty("content", content);
    var contentJson = Constants.get().getGson().toJson(o);
    var httpRequest =
        HttpRequest.newBuilder(NekobinUploader.uri)
            .POST(HttpRequest.BodyPublishers.ofString(contentJson))
            .setHeader("Content-Type", "application/json")
            .build();

    return Constants.get()
        .getHttpClient()
        .sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString())
        .thenApply(HttpResponse::body)
        .thenApply(body -> Constants.get().getGson().fromJson(body, NekobinResult.class))
        .toCompletableFuture();
  }

  @Data
  public static final class NekobinDocument {
    @SerializedName("key")
    private String key;

    @SerializedName("title")
    private String title;

    @SerializedName("author")
    private String author;

    @SerializedName("date")
    private String date;

    @SerializedName("views")
    private int views;

    @SerializedName("length")
    private int length;

    @SerializedName("content")
    private String content;

    public String asUrl() {
      return "https://nekobin.com/" + this.key;
    }
  }

  @Data
  public static final class NekobinResult {
    @SerializedName("ok")
    private boolean ok;

    @SerializedName("result")
    private NekobinDocument document;

    @SerializedName("error")
    private String error;
  }
}
