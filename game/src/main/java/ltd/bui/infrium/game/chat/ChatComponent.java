package ltd.bui.infrium.game.chat;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.core.api.components.Component;
import ltd.bui.infrium.game.Settlements;
import ltd.bui.infrium.game.chat.global.GlobalChannel;
import ltd.bui.infrium.game.components.testing.commands.TestCommand;
import ltd.bui.infrium.game.components.testing.listeners.TestListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;

public class ChatComponent extends Component<Settlements> {

    @Getter @Setter public static ChatComponent instance;
    private GlobalChannel gc;

    @Getter private Set<Channel> channels = null;

    //party, [P] <prefix>name<suffix>: <message>
    //private @<prefix>name<suffix>: <message>
    //local <<prefix>name<suffix>: <message>
    //global [G] <<prefix>name<suffix> : <message>
    //echo
    //echo  (ECHO)  -->  (serverid){game,server,all}
    //echo              ...<message>
    //echo              ...<message>
    //echo                              (ECHO_END)
    //echo


    public ChatComponent() {
        instance = this;

    }

    @Override
    public void enable(Settlements plugin) {
        if (instance == null) instance = new ChatComponent();
        if (channels == null) channels = new HashSet<>();
    }

    @Override
    public void disable(Settlements plugin) {

    }

    @Override
    public void registerListener(Settlements plugin) {
        new TestListener(plugin);
    }

    @Override
    public void registerCommands(Settlements plugin) {
        new TestCommand();
    }


    @EventHandler
    public void onChatEvent(AsyncPlayerChatEvent event) {
        channels.forEach(channel -> channel.processMessage(event.getMessage()));

    }



}
