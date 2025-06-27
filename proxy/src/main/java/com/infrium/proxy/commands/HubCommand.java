package com.infrium.proxy.commands;


import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.infrium.api.hive.enums.ServerType;
import com.infrium.proxy.Proxy;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class HubCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {

            Player player = ((Player) invocation.source());
            player.sendMessage(MiniMessage.miniMessage().deserialize("Sending to hub"));

        Proxy.getQueueRepository().joinQueue(player.getUsername(), ServerType.LOBBY);

    }

}
