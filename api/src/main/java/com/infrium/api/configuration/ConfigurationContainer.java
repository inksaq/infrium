package com.infrium.api.configuration;

import lombok.NonNull;

import java.io.File;
import java.io.IOException;

public abstract class ConfigurationContainer<T> {

  protected T configuration;

  public ConfigurationContainer(@NonNull final T conf) {
    this.configuration = conf;
  }

  public abstract int getInteger(String key);

  public abstract int getInteger(String key, int defaultValue);

  public abstract String getString(String key);

  public abstract String getString(String key, String defaultValue);

  public abstract boolean getBoolean(String key);

  public abstract boolean getBoolean(String key, boolean defaultValue);

  public abstract double getDouble(String key);

  public abstract double getDouble(String key, double defaultValue);

  public abstract <T> T get(String key, Class<T> clazz, T defaultValue);

  public abstract <T> T get(String key, Class<T> clazz);

  public abstract void setKey(String key, Object value);

  public abstract void save(String path) throws IOException;

  public abstract void save(File path) throws IOException;

  public abstract void save() throws IOException;

  public abstract File getFile();

  public abstract void reload(boolean save) throws Exception;
}
