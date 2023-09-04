package ltd.bui.infrium.game.components.weapon.gun.commands;

import ltd.bui.infrium.core.api.command.Command;
import ltd.bui.infrium.core.api.command.CommandArgs;
import ltd.bui.infrium.game.BaseCommand;
import ltd.bui.infrium.game.components.weapon.gun.GunRegistry;

public class GetWeaponCC extends BaseCommand {


    @Command(name = "getweapon", description = "get weapon")
    public void onCommand(CommandArgs args) throws Exception {
        if (args.getArgs(0).equalsIgnoreCase("scar90"))
            GunRegistry.giveGunToPlayer(args.getPlayer(), "scar90");
    }
}
