package ltd.bui.infrium.game.components.weapon.gun.commands;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import ltd.bui.infrium.core.api.command.Command;
import ltd.bui.infrium.core.api.command.CommandArgs;
import ltd.bui.infrium.game.BaseCommand;
import ltd.bui.infrium.game.Settlements;
import ltd.bui.infrium.game.components.testing.ui.WorkbenchGUI;
import ltd.bui.infrium.game.components.weapon.WeaponComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.ChargeCell;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.EnergyCore;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell.FastCharge;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell.OverCharge;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell.SuperVolt;
import ltd.bui.infrium.game.components.weapon.energy.components.gui.EnergyWeaponBuilder;
import ltd.bui.infrium.game.components.weapon.gun.WeaponListener;
import ltd.bui.infrium.game.components.weapon.gun.armory.SCAR90;
import ltd.bui.infrium.game.components.weapon.registry.WeaponRegistry;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.util.Set;
import java.util.function.Function;

public class GetWeaponCC extends BaseCommand {


    @Command(name = "wp", description = "get weapon")
    public void onCommand(CommandArgs args) throws Exception {
        Player player = (Player) args.getSender();
        if (args.getArgs(0).equalsIgnoreCase("scar")){
            player.getInventory().addItem(new SCAR90("scar90").getItem());
        }

        if (args.getArgs(0).equalsIgnoreCase("fb")) {
//            GunRegistry.giveGunToPlayer(args.getPlayer(), "scar90");
            FrameBody facFB = new FrameBody(Grade.FACTORY, Rarity.BLACKMARKET, Tier.IV);
            EnergyCore energyCore = new EnergyCore(Rarity.PRIME, Grade.FACTORY, Tier.II);
            ChargeCell chargeCell = new ChargeCell(Rarity.PRIME, Grade.PRIMED, Tier.IV);

            facFB.addEnergyCore(energyCore);
            facFB.getEnergyCore().onTick();
            facFB.addChargeCell(chargeCell);
            facFB.getChargeCell().addUpgrade(new FastCharge(Rarity.BLACKMARKET, Grade.PRIMED, Tier.IV));
            facFB.getChargeCell().onTick();
            WeaponComponent.getInstance().getWeaponRegistry().registerWeapon(facFB);
            facFB.set(EnergyWeaponBuilder.builder().setFrameBody(facFB).build(true), facFB, player);
//            player.getInventory().addItem(EnergyWeaponBuilder.builder(Grade.FACTORY).setFrameBody(facFB).build(true));
        }
        if (args.getArgs(0).equalsIgnoreCase("f")){
            FrameBody frameBody = new FrameBody(Grade.FACTORY, Rarity.BLACKMARKET, Tier.II);
//            EnergyCore energyCore = new EnergyCore(frameBody, Rarity.MASS, Grade.SHATTERED, Tier.I);

            ChargeCell cg = new ChargeCell(Rarity.MASS, Grade.SHATTERED, Tier.I);

            frameBody.addChargeCell(cg);
//            frameBody.addEnergyCore(energyCore);
            frameBody.tickFrameBody();
            frameBody.debug();
            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().put(frameBody.getFrameUUID(), frameBody);

        }
        if (args.getArgs(0).equalsIgnoreCase("tick")) {
            ;
            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().forEach((uuid, frameBody) -> frameBody.tickFrameBody());
            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().forEach((uuid, frameBody) -> frameBody.debug());
        }
//        if (args.getArgs(0).equalsIgnoreCase("a")) {
//
//            EnergyCore energyCore = new EnergyCore(Rarity.PRIME, Grade.FACTORY, Tier.II);
//
//            EnergyWeaponBuilder.builder().setEnergyCore(energyCore).build(true);
//        }

        if (args.getArgs(0).equalsIgnoreCase("ra")) {
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            if (WeaponComponent.getInstance().getWeaponRegistry().getFrameBody(mainHand) != null){
                var framebody = WeaponComponent.getInstance().getWeaponRegistry().getFrameBody(mainHand);
                framebody.tickFrameBody(mainHand, framebody);
                framebody.debug();
                if (framebody.isOperational()) {
                    player.sendMessage("not operational");
                }
            }

        }
        if (args.getArgs(0).equalsIgnoreCase("rb")) {
            WeaponRegistry.getInstance().getFramebodies().forEach((uuid, frameBody) -> {System.out.println(uuid.toString() + "\n");
            frameBody.debug();});
        }
        if (args.getArgs(0).equalsIgnoreCase("rc")) {
            var gui = new WorkbenchGUI(LegacyComponentSerializer.legacyAmpersand().deserialize("Weapon"), Settlements.getInstance());
            gui.openInventory(player);
        }

        if (args.getArgs(0).equalsIgnoreCase("rl")) {
            ItemStack itemstack = player.getInventory().getItemInMainHand();
            Set<String> keys = NBT.get(itemstack, ReadableItemNBT::getKeys);
            for (String key : keys) {
                String value = NBT.get(itemstack, (Function<ReadableItemNBT, String>) nbt -> nbt.getString(key));
                player.sendMessage(key + ": " + value);
            }        }

        if (args.getArgs(0).equalsIgnoreCase("rf")) {
            ItemStack itemstack = player.getInventory().getItemInMainHand();
//            if (itemstack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Settlements.getInstance(), WeaponComponent.getInstance().getWeaponKey()), WeaponComponent.getInstance().getFrameBodyDataType())
            String uuid = NBT.get(itemstack, (Function<ReadableItemNBT, String>) nbt -> nbt.getString("uuid"));
            if (uuid != null) player.sendMessage(uuid);
            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().forEach((uuidd, fb) -> System.out.println(uuid + " : " + fb.getFrameUUID()));
        }
            if (args.getArgs(0).equalsIgnoreCase("r")) {
            ItemStack itemstack = player.getInventory().getItemInMainHand();
            String uuid = NBT.get(itemstack, (Function<ReadableItemNBT, String>) nbt -> nbt.getString("uuid"));
                var fb = WeaponComponent.getInstance().getWeaponRegistry().getFrameBody(itemstack);
//            FrameBody fb = WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().get(uuid).getFrameBody();

            EnergyCore energyCore = fb.getEnergyCore();
            ChargeCell chargeCell = fb.getChargeCell();
            fb.debug();
            chargeCell.chargeCore();
            energyCore.onTick();
            energyCore.expendEnergy();
            energyCore.onTick();
            fb.debug();
            args.getSender().sendMessage("simulation complete of :" + fb.getFrameUUID());
            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().put(fb.getFrameUUID(), fb);
        }


    }
}
