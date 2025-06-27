package com.infrium.api.configuration;

public interface IConfigurationEnum {

  default int getInt() {
    return getConfig().getInteger(this.getKey(), (int) getDefaultValue());
  }

  default double getDouble() {
    return getConfig().getDouble(this.getKey(), (double) getDefaultValue());
  }

  default boolean getBoolean() {
    return getConfig().getBoolean(this.getKey(), (boolean) getDefaultValue());
  }

  default String getString() {
    return getConfig().getString(this.getKey(), (String) getDefaultValue());
  }

  default <T> T get(Class<T> c) {
    return c.cast(getConfig().get(this.getKey(), c, c.cast(getDefaultValue())));
  }

  default void set(Object value) {
    getConfig().setKey(this.getKey(), value);
  }

  String getKey();

  Object getDefaultValue();

  ConfigurationContainer<?> getConfig();
}
