package com.infrium.core.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import org.bukkit.entity.Player;


public class ServerCommand extends BaseCommand {

    @CommandAlias("shard|s|server|servers")
    public void openShardMenu(Player p) {
        com.infrium.core.ICore.getInstance().getServerSelectorGUI().openInventory(p);
    }
}
