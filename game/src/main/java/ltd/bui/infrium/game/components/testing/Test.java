package ltd.bui.infrium.game.components.testing;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.api.player.AbstractInfriumPlayer;
import ltd.bui.infrium.core.api.components.Component;
import ltd.bui.infrium.game.Settlements;
import ltd.bui.infrium.game.components.testing.commands.TestCommand;
import ltd.bui.infrium.game.components.testing.listeners.TestListener;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Test extends Component<Settlements> {

    @Getter @Setter public static Test instance;

    @Getter
    @Setter
    private Set<AbstractInfriumPlayer<Player>> loadedPlayers;

    public Test() {
        instance = this;

    }

    @Override
    public void enable(Settlements plugin) {
        if (instance == null) instance = new Test();
        loadedPlayers = new HashSet<>();
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
}
