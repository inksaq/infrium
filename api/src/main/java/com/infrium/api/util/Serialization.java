package com.infrium.api.util;

import lombok.NonNull;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Serialization {

  @NonNull
  public static String serialize(@NonNull Serializable obj) {
    return new String(SerializationUtils.serialize(obj), StandardCharsets.ISO_8859_1);
  }

  public static <T> T deserialize(@NonNull String serialized) {
    return SerializationUtils.deserialize(serialized.getBytes(StandardCharsets.ISO_8859_1));
  }

  public static String serializeCompressed(@NonNull Serializable obj) throws Exception {
    return new String(compress(serialize(obj)), StandardCharsets.ISO_8859_1);
  }

  public static <T> T deserializeCompressed(@NonNull String serialized) throws Exception {
    return deserialize(decompress(serialized.getBytes(StandardCharsets.ISO_8859_1)));
  }

  // https://gist.github.com/yblee85/70d5bba26196e9dc4270
  public static byte[] compress(@NonNull String str) throws Exception {
    ByteArrayOutputStream obj = new ByteArrayOutputStream();
    GZIPOutputStream gzip = new GZIPOutputStream(obj);
    gzip.write(str.getBytes(StandardCharsets.ISO_8859_1));
    gzip.close();
    return obj.toByteArray();
  }

  // https://gist.github.com/yblee85/70d5bba26196e9dc4270
  public static String decompress(@NonNull byte[] data) throws Exception {
    GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(data));
    BufferedReader bf = new BufferedReader(new InputStreamReader(gis, StandardCharsets.ISO_8859_1));
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = bf.readLine()) != null) {
      sb.append(line);
    }
    return sb.toString();
  }
}
