package ltd.bui.infrium.game.components.weapon.gun.commands;

import ltd.bui.infrium.core.api.command.Command;
import ltd.bui.infrium.core.api.command.CommandArgs;
import ltd.bui.infrium.game.BaseCommand;
import ltd.bui.infrium.game.components.weapon.WeaponComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.ChargeCell;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.EnergyCore;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell.OverCharge;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GetWeaponCC extends BaseCommand {


    @Command(name = "wp", description = "get weapon")
    public void onCommand(CommandArgs args) throws Exception {
        Player player = (Player) args.getSender();

        if (args.getArgs(0).equalsIgnoreCase("fb")) {
//            GunRegistry.giveGunToPlayer(args.getPlayer(), "scar90");
            FrameBody facFB = new FrameBody(Grade.FACTORY);
            EnergyCore energyCore = new EnergyCore(Rarity.PRIME, Grade.FACTORY, Tier.II);
            facFB.addEnergyCore(energyCore);
            facFB.set(new ItemStack(Material.DIAMOND_HOE, 1), facFB, player);
//            player.getInventory().addItem(EnergyWeaponBuilder.builder(Grade.FACTORY).setFrameBody(facFB).recompute().build(true));
        }
        if (args.getArgs(0).equalsIgnoreCase("f")){
            FrameBody frameBody = new FrameBody(Grade.FACTORY);
//            EnergyCore energyCore = new EnergyCore(frameBody, Rarity.MASS, Grade.SHATTERED, Tier.I);

            ChargeCell cg = new ChargeCell(frameBody, Rarity.MASS, Grade.SHATTERED, Tier.I);

            frameBody.addChargeCell(cg);
//            frameBody.addEnergyCore(energyCore);
            frameBody.tickFrameBody(frameBody);
            frameBody.debug();
            WeaponComponent.getInstance().getFramebodies().put(frameBody.getFrameUUID(), frameBody);

        }
        if (args.getArgs(0).equalsIgnoreCase("tick")) {
            ;
            WeaponComponent.getInstance().getFramebodies().forEach((uuid, frameBody) -> frameBody.tickFrameBody(frameBody));
            WeaponComponent.getInstance().getFramebodies().forEach((uuid, frameBody) -> frameBody.debug());
        }
        if (args.getArgs(0).equalsIgnoreCase("a")) {

            FrameBody fb = WeaponComponent.getInstance().getFrameBody(Integer.valueOf(args.getArgs(1)));
            var cvoerCharge = new OverCharge(Rarity.MASS, Grade.SHATTERED, Tier.I);
            if (fb.getChargeCell().addUpgrade(cvoerCharge)) {
                args.getSender().sendMessage("upgrade complete " + fb.getChargeCell().getUpgrades().size() + " / " + fb.getChargeCell().getUpgradeLimit());               ;
            } else {
                args.getSender().sendMessage("can't apply upgrade, limit hit" + fb.getChargeCell().getUpgrades().size() + " / " + fb.getChargeCell().getUpgradeLimit());
            }
        }
        if (args.getArgs(0).equalsIgnoreCase("e")) {
            FrameBody fb = WeaponComponent.getInstance().getFrameBody(Integer.valueOf(args.getArgs(1)));
            if (args.getArgs(1).equalsIgnoreCase("a")){
                EnergyCore energyCore1 = new EnergyCore(Rarity.PRIME, Grade.PRIMED, Tier.II);
                fb.addEnergyCore(energyCore1);
                return;
            }

            var energyCore = fb.getEnergyCore();
            if (energyCore == null) return;



            fb.debug();
            energyCore.expendEnergy();
            energyCore.onTick();
            args.getSender().sendMessage("simulation complete of :" + fb.getFrameUUID());
            WeaponComponent.getInstance().getFramebodies().put(fb.getFrameUUID(), fb);
        }
        if (args.getArgs(0).equalsIgnoreCase("r")) {

            FrameBody fb = WeaponComponent.getInstance().getFrameBody(Integer.valueOf(args.getArgs(1)));
            EnergyCore energyCore = new EnergyCore(Rarity.PRIME, Grade.PRIMED, Tier.II);
            fb.getChargeCell().chargeCore();
            fb.debug();
            energyCore.onTick();
            energyCore.expendEnergy();
            energyCore.onTick();
            fb.debug();
            args.getSender().sendMessage("simulation complete of :" + fb.getFrameUUID());
            WeaponComponent.getInstance().getFramebodies().put(fb.getFrameUUID(), fb);
        }


    }
}
