package com.infrium.lobby.configuration;


import lombok.Getter;
import com.infrium.api.configuration.ConfigurationContainer;
import com.infrium.api.configuration.IConfigurationEnum;
import com.infrium.lobby.InfriumLobby;
import org.bukkit.Location;

import static com.infrium.api.util.LangUtils.listOf;


public enum LobbyConfiguration implements IConfigurationEnum {
  HUB_SPAWN("hub", new Location(null,-2.5, 87.5, 103, 90, 0)),
  SCOREBOARD("scoreboard", listOf("TITLE", "LINE1", "LINE2")),
  ;

  @Getter private final String key;
  @Getter private final Object defaultValue;

  LobbyConfiguration(String key, Object defaultValue) {
    this.key = key;
    this.defaultValue = defaultValue;
  }

  public final ConfigurationContainer<?> getConfig() {
    return InfriumLobby.getInstance().getConfiguration();
  }
}
