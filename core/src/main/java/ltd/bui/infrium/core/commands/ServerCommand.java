package ltd.bui.infrium.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import ltd.bui.infrium.core.InfriumCore;
import ltd.bui.infrium.core.Teleporter;
import org.bukkit.entity.Player;


public class ServerCommand extends BaseCommand {

    @CommandAlias("shard|s|server|servers")
    public void openShardMenu(Player p) {
        InfriumCore.getInstance().getServerSelectorGUI().openInventory(p);
    }
}
