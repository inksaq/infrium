package ltd.bui.infrium.game.chat.global;

import ltd.bui.infrium.game.chat.Channel;
import ltd.bui.infrium.game.chat.ChannelType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class GlobalChannel extends Channel implements Listener {

    private static Set<Player> globalPlayerList;

    private ChannelType channelType;

    @Override
    protected void processMessage(String message) {

        globalPlayerList.forEach(player -> player.sendRawMessage(message));
        System.out.println("processing raw message to global channel listed players");
    }

    public GlobalChannel(ChannelType channelType) {
        super(channelType);
        this.channelType = channelType;
        init();
    }

    public void init() {
        globalPlayerList = new HashSet<>();
    }

    @EventHandler
    public void addGlobalRecievers(PlayerLoginEvent event) {
        if (globalPlayerList == null) return;
        globalPlayerList.add(event.getPlayer());
    }

    @EventHandler
    public void removeGlobalReciever(PlayerQuitEvent event) {
        if (globalPlayerList == null) return;
        globalPlayerList.remove(event.getPlayer());
    }
}
