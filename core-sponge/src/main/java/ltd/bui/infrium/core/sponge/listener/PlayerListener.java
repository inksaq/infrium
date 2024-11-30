package ltd.bui.infrium.core.sponge.listener;

import ltd.bui.infrium.core.sponge.InfriumCoreSponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

public class PlayerListener {

    private final InfriumCoreSponge plugin;

    public PlayerListener(InfriumCoreSponge plugin) {
        this.plugin = plugin;
    }

    @Listener
    public void onPlayerJoin(ServerSideConnectionEvent.Join event) {
        Player player = event.player();
        // TODO: Implement InfriumScoreBoard.createScore(player) equivalent for Sponge
//        plugin.setupPrefix(player);
    }

    @Listener
    public void onPlayerQuit(ServerSideConnectionEvent.Disconnect event) {
        // TODO: Implement any necessary cleanup on player quit
    }
}

