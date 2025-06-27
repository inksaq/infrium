package com.infrium.core.configuration;

import com.infrium.api.configuration.ConfigurationContainer;
import com.infrium.api.configuration.IConfigurationEnum;
import lombok.Getter;

@Getter
public enum CoreConfiguration implements IConfigurationEnum {
  CHAT_ENABLED("chat.enabled", true), // is chat enabled ?
  CHAT_FILTER_REPETITION("chat.filter.repetition", true), // anti cheat repetition
  CHAT_FILTER_SWEAR("chat.filter.swear", true), // anti swear
  CHAT_FILTER_ONLY_STAFF("chat.filter.only-staff", false), // only staff can chat
  CHAT_GLOBAL_ENABLED("chat.global.enabled", true), // global chat
  CHAT_GLOBAL_PREFIX("chat.global.prefix", "-"), // global chat prefix
  ;

  private final String key;
  private final Object defaultValue;

  CoreConfiguration(String key, Object defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;
  }

  @Override
  public final ConfigurationContainer<?> getConfig() {
    return com.infrium.core.ICore.getInstance().getConfiguration();
  }
}
