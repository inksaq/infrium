package com.infrium.game.components.chat.commands;

import com.infrium.core.api.command.Command;
import com.infrium.core.api.command.CommandArgs;
import com.infrium.game.BaseCommand;
import org.bukkit.entity.Player;

public class GlobalChatCommand extends BaseCommand {

    @Command(name = "gc")
    public void onCommand(CommandArgs args) {
        args.getSender().sendMessage("<G> " + args.getSender().getName() + ": " + args.getArgs());
        Player player = args.getPlayer();

    }




}
