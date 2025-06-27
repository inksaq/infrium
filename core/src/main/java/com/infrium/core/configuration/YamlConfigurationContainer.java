package com.infrium.core.configuration;

import com.infrium.api.configuration.ConfigurationContainer;
import lombok.NonNull;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YamlConfigurationContainer extends ConfigurationContainer<YamlConfiguration> {

  private final File file;

  public YamlConfigurationContainer(@NonNull YamlConfiguration conf, File file) {
    super(conf);
    this.file = file;
  }

  @Override
  public int getInteger(String key) {
    return this.configuration.getInt(key);
  }

  @Override
  public int getInteger(String key, int defaultValue) {
    return this.configuration.getInt(key, defaultValue);
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
  public <T> T get(String key, Class<T> clazz) {
    return this.configuration.getObject(key, clazz);
  }

  @Override
  public <T> T get(String key, Class<T> clazz, T defaultValue) {
    return this.configuration.getObject(key, clazz, defaultValue);
  }

  @Override
  public void setKey(String key, Object value) {
    this.configuration.set(key, value);
  }

  @Override
  public void save(String path) throws IOException {
    this.configuration.save(path);
  }

  @Override
  public void save(File path) throws IOException {
    this.configuration.save(path);
  }

  @Override
  public void save() throws IOException {
    this.configuration.save(this.file);
  }

  @Override
  public File getFile() {
    return this.file;
  }

  @Override
  public void reload(boolean save) throws IOException, InvalidConfigurationException {
    if (save) {
      this.configuration.save(this.file);
    }
    this.configuration.load(this.file);
  }
}
