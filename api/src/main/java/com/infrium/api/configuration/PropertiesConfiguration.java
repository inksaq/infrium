package com.infrium.api.configuration;

import lombok.Cleanup;
import lombok.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertiesConfiguration extends ConfigurationContainer<Properties> {

  private final File file;

  public PropertiesConfiguration(@NonNull Properties conf, File file) {
    super(conf);
    this.file = file;
  }

  @Override
  public int getInteger(String key) {
    return (int) this.configuration.get(key);
  }

  @Override
  public int getInteger(String key, int defaultValue) {
    return (int) this.configuration.getOrDefault(key, defaultValue);
  }

  @Override
  public String getString(String key) {
    return this.configuration.getProperty(key);
  }

  @Override
  public String getString(String key, String defaultValue) {
    return this.configuration.getProperty(key, defaultValue);
  }

  @Override
  public boolean getBoolean(String key) {
    return (boolean) this.configuration.get(key);
  }

  @Override
  public boolean getBoolean(String key, boolean defaultValue) {
    return (boolean) this.configuration.getOrDefault(key, defaultValue);
  }

  @Override
  public double getDouble(String key) {
    return (double) this.configuration.get(key);
  }

  @Override
  public double getDouble(String key, double defaultValue) {
    return (double) this.configuration.getOrDefault(key, defaultValue);
  }

  @Override
  public <T> T get(String key, Class<T> clazz) {
    return clazz.cast(this.configuration.get(key));
  }

  @Override
  public <T> T get(String key, Class<T> clazz, T defaultValue) {
    return clazz.cast(this.configuration.getOrDefault(key, defaultValue));
  }

  @Override
  public void setKey(String key, Object value) {
    this.configuration.put(key, value);
  }

  @Override
  public void save(String path) throws IOException {
    @Cleanup var fw = new FileWriter(path);
    this.configuration.store(fw, null);
  }

  @Override
  public void save(File path) throws IOException {
    @Cleanup var fw = new FileWriter(path);
    this.configuration.store(fw, null);
  }

  @Override
  public void save() throws IOException {
    @Cleanup var fw = new FileWriter(this.file);
    this.configuration.store(fw, null);
  }

  @Override
  public File getFile() {
    return this.file;
  }

  @Override
  public void reload(boolean save) throws Exception {
    throw new RuntimeException(new IllegalAccessException("Not implemented"));
  }
}
