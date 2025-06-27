package com.infrium.proxy.configuration;

import com.moandjiezana.toml.Toml;
import lombok.NonNull;
import com.infrium.api.configuration.ConfigurationContainer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TomlConfigurationContainer extends ConfigurationContainer<Toml> {

  public TomlConfigurationContainer(@NonNull Toml conf) {
    super(conf);
  }

  @Override
  public int getInteger(String key) {
    return this.configuration.getLong(key).intValue();
  }

  @Override
  public int getInteger(String key, int defaultValue) {
    return this.configuration.getLong(key, (long) defaultValue).intValue();
  }

  @Override
  public String getString(String key) {
    return this.configuration.getString(key);
  }

  @Override
  public String getString(String key, String defaultValue) {
    return this.configuration.getString(key, defaultValue);
  }

  @Override
  public boolean getBoolean(String key) {
    return this.configuration.getBoolean(key);
  }

  @Override
  public boolean getBoolean(String key, boolean defaultValue) {
    return this.configuration.getBoolean(key, defaultValue);
  }

  @Override
  public double getDouble(String key) {
    return this.configuration.getDouble(key);
  }

  @Override
  public double getDouble(String key, double defaultValue) {
    return this.configuration.getDouble(key, defaultValue);
  }

  @Override
  public <T> T get(String key, Class<T> clazz, T defaultValue) {
    throw new IllegalArgumentException("Not supported");
  }

  @Override
  public <T> T get(String key, Class<T> clazz) {
    throw new IllegalArgumentException("Not supported");
  }

  @Override
  public void setKey(String key, Object value) {
    throw new IllegalArgumentException("Not supported");
  }

  @Override
  public void save(String path) throws IOException {
    File file = new File(path);
    FileWriter writer = new FileWriter(file);
    try {
      // This is a hypothetical method; you should replace it with the actual method
      // provided by your TOML library to convert the configuration to a string
      String tomlString = this.configuration.toString();
      writer.write(tomlString);
    } finally {
      writer.close();
    }  }

  @Override
  public void save(File path) throws IOException {
    save(path.getAbsolutePath());
  }

  @Override
  public void save() throws IOException {
    throw new IllegalArgumentException("Not supported");
  }

  @Override
  public File getFile() {
    throw new IllegalArgumentException("Not supported");
  }

  @Override
  public void reload(boolean save) throws Exception {
    throw new IllegalArgumentException("Not supported");
  }
}
