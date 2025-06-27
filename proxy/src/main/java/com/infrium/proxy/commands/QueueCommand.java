package com.infrium.proxy.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.infrium.api.hive.enums.QueueLeftReason;
import com.infrium.api.hive.enums.ServerType;
import org.apache.commons.lang3.StringUtils;

import static com.infrium.proxy.Proxy.*;

public class QueueCommand implements SimpleCommand {

  @Override
  public void execute(Invocation invocation) {
    if (invocation.source() instanceof Player) {
      Player player = (Player) invocation.source();
      var infriumPlayer = getInfriumProvider().getInfriumPlayer(player);
      if (infriumPlayer.isPresent()) {
        getQueueRepository().getPlayerQueue(player.getUsername()).thenAccept(optionalQueue -> {
                  if (optionalQueue.isPresent()) {
                    getQueueRepository().leaveQueue(player.getUsername(), QueueLeftReason.LEFT);
                    player.sendMessage(
                        serialize.apply(
                            "&6&lQueue &8"
                                + HEAVY_VERTICAL
                                + " &7You have left the queue for %s."
                                    .formatted(
                                        StringUtils.capitalize(
                                            optionalQueue.get().name()))));

                  } else {
                    if (invocation.arguments().length >= 1) {
                      var queue = invocation.arguments()[0];
                      try {
                        var queueType = ServerType.valueOf(queue.toUpperCase());
                        getQueueRepository().joinQueue(player.getUsername(), queueType).thenAccept(__ ->
                                    player.sendMessage(
                                        serialize.apply(
                                            "&6&lQueue &8"
                                                + HEAVY_VERTICAL
                                                + " &7You joined the queue for %s."
                                                    .formatted(
                                                        StringUtils.capitalize(
                                                            optionalQueue
                                                                .get()
                                                                .name())))));
                      } catch (IllegalArgumentException e) {
                        player.sendMessage(
                            serialize.apply(
                                "&6&lQueue &8" + HEAVY_VERTICAL + " &7Queue not found."));
                      }
                    }
                  }
                });

      } else {
        player.sendMessage(
            serialize.apply("&6&lSystem &8" + HEAVY_VERTICAL + " &7Unable to fetch your data."));
      }
    } else {
      invocation
          .source()
          .sendMessage(
              serialize.apply(
                  "&6&lSystem &8"
                      + HEAVY_VERTICAL
                      + " &7You must be a player to use this command."));
    }
  }

  @Override
  public boolean hasPermission(Invocation invocation) {
    return SimpleCommand.super.hasPermission(invocation);
  }
}
