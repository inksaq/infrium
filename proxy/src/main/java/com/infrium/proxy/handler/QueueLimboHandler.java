package com.infrium.proxy.handler;

import lombok.Getter;

import com.infrium.api.hive.enums.ServerType;
import com.infrium.proxy.Proxy;
import com.infrium.proxy.listener.ServerListener;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.LimboPlayer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class QueueLimboHandler implements LimboSessionHandler {

  private final BossBar bossBar =
      BossBar.bossBar(
          MiniMessage.miniMessage().deserialize(
                  "<bold><gradient:#95FCFE:#C089F0>SEARCHING FOR A LOBBY SERVER</gradient></bold>"),
          1,
          BossBar.Color.PURPLE,
          BossBar.Overlay.PROGRESS);
  @Getter private LimboPlayer player;

  @Override
  public void onSpawn(Limbo server, LimboPlayer player) {
    this.player = player;
    this.player.disableFalling();

    player.getProxyPlayer().showBossBar(bossBar);

    if (!ServerListener.checkPunishments(player.getProxyPlayer())) {
      Proxy.get().registerQueueLimbo(player.getProxyPlayer().getUsername(), this);
      Proxy
          .getQueueRepository()
          .joinQueue(player.getProxyPlayer().getUsername(), ServerType.LOBBY);
    }
  }



  @Override
  public void onDisconnect() {
    this.player.getProxyPlayer().hideBossBar(bossBar);
    Proxy.get().getInfriumProvider().onQuit(player.getProxyPlayer());

    Proxy.get().unregisterQueueLimbo(player.getProxyPlayer().getUsername());
    Proxy.get().getQueueRepository().leaveQueue(player.getProxyPlayer().getUsername());
  }
}
