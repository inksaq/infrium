package ltd.bui.infrium.game.components.chat.commands;

import ltd.bui.infrium.core.api.command.Command;
import ltd.bui.infrium.core.api.command.CommandArgs;
import ltd.bui.infrium.game.BaseCommand;
import org.bukkit.entity.Player;

public class GlobalChatCommand extends BaseCommand {

    @Command(name = "gc")
    public void onCommand(CommandArgs args) {
        args.getSender().sendMessage("<G> " + args.getSender().getName() + ": " + args.getArgs());
        Player player = args.getPlayer();

    }




}
