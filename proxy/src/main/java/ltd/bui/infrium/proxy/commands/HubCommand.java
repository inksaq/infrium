package ltd.bui.infrium.proxy.commands;


import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import ltd.bui.infrium.api.hive.enums.QueueChannels;
import ltd.bui.infrium.api.hive.enums.ServerType;
import ltd.bui.infrium.api.hive.pubsub.queue.RedisQueueConnect;
import ltd.bui.infrium.api.hive.servers.Server;
import ltd.bui.infrium.api.player.AbstractInfriumPlayer;
import ltd.bui.infrium.proxy.Proxy;
import ltd.bui.infrium.proxy.ProxyQueueRepository;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.Optional;

public class HubCommand implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {

            Player player = ((Player) invocation.source());
            player.sendMessage(MiniMessage.miniMessage().deserialize("Sending to hub"));

        Proxy.getQueueRepository().joinQueue(player.getUsername(), ServerType.LOBBY);

    }

}
