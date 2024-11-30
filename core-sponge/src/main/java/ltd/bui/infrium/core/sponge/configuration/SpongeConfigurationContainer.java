package ltd.bui.infrium.core.sponge.configuration;

import ltd.bui.infrium.api.configuration.ConfigurationContainer;
import lombok.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.io.IOException;

public class SpongeConfigurationContainer extends ConfigurationContainer<CommentedConfigurationNode> {

  private final File file;
  private final HoconConfigurationLoader loader;

  public SpongeConfigurationContainer(@NonNull CommentedConfigurationNode conf, File file) {
    super(conf);
    this.file = file;
    this.loader = HoconConfigurationLoader.builder().file(file).build();
  }

  @Override
  public int getInteger(String key) {
    return this.configuration.node(key.split("\\.")).getInt();
  }

  @Override
  public int getInteger(String key, int defaultValue) {
    return this.configuration.node(key.split("\\.")).getInt(defaultValue);
  }

  @Override
  public String getString(String key) {
    return this.configuration.node(key.split("\\.")).getString();
  }

  @Override
  public String getString(String key, String defaultValue) {
    return this.configuration.node(key.split("\\.")).getString(defaultValue);
  }

  @Override
  public boolean getBoolean(String key) {
    return this.configuration.node(key.split("\\.")).getBoolean();
  }

  @Override
  public boolean getBoolean(String key, boolean defaultValue) {
    return this.configuration.node(key.split("\\.")).getBoolean(defaultValue);
  }

  @Override
  public double getDouble(String key) {
    return this.configuration.node(key.split("\\.")).getDouble();
  }

  @Override
  public double getDouble(String key, double defaultValue) {
    return this.configuration.node(key.split("\\.")).getDouble(defaultValue);
  }

  @Override
  public <T> T get(String key, Class<T> clazz) {
    try {
      return this.configuration.node(key.split("\\.")).get(clazz);
    } catch (SerializationException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public <T> T get(String key, Class<T> clazz, T defaultValue) {
    try {
      return this.configuration.node(key.split("\\.")).get(clazz, defaultValue);
    } catch (SerializationException e) {
      e.printStackTrace();
      return defaultValue;
    }
  }

  @Override
  public void setKey(String key, Object value) {
    try {
      this.configuration.node(key.split("\\.")).set(value);
    } catch (SerializationException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void save(String path) throws IOException {
    loader.save(this.configuration);
  }

  @Override
  public void save(File path) throws IOException {
    HoconConfigurationLoader tempLoader = HoconConfigurationLoader.builder().file(path).build();
    tempLoader.save(this.configuration);
  }

  @Override
  public void save() throws IOException {
    loader.save(this.configuration);
  }

  @Override
  public File getFile() {
    return this.file;
  }

  @Override
  public void reload(boolean save) throws IOException {
    if (save) {
      save();
    }
    this.configuration = loader.load();
  }
}