package ltd.bui.infrium.api.configuration;

import lombok.Getter;

public enum InfriumConfiguration implements IConfigurationEnum {
  MONGODB_URI("mongo_uri", "mongodb://192.168.1.31:27017"),
  REDIS_URI("redis_uri", "redis://sentry:data123@192.168.1.32:6379/0"),;

  private static ConfigurationContainer<?> configurationContainer;

  @Getter private final String key;
  @Getter private final Object defaultValue;

  InfriumConfiguration(String key, Object defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;
  }

  public static void setConfigurationContainer(ConfigurationContainer<?> configurationContainer) {
    InfriumConfiguration.configurationContainer = configurationContainer;
  }

  @Override
  public ConfigurationContainer<?> getConfig() {
    return configurationContainer;
  }
}
