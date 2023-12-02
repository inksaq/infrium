package ltd.bui.infrium.game.components.weapon.gun.commands;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableItemNBT;
import ltd.bui.infrium.core.api.command.Command;
import ltd.bui.infrium.core.api.command.CommandArgs;
import ltd.bui.infrium.game.BaseCommand;
import ltd.bui.infrium.game.components.weapon.WeaponComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.ChargeCell;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.EnergyCore;
import ltd.bui.infrium.game.components.weapon.energy.components.gui.EnergyWeaponBuilder;
import ltd.bui.infrium.game.components.weapon.gun.armory.SCAR90;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
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
            FrameBody facFB = new FrameBody(Grade.FACTORY);
            EnergyCore energyCore = new EnergyCore(Rarity.PRIME, Grade.FACTORY, Tier.II);
            facFB.addEnergyCore(energyCore);
            WeaponComponent.getInstance().getWeaponRegistry().registerWeapon(facFB);
//            facFB.set(new ItemStack(Material.DIAMOND_HOE, 1), facFB, player);
            player.getInventory().addItem(EnergyWeaponBuilder.builder(Grade.FACTORY).setFrameBody(facFB).build(true));
        }
        if (args.getArgs(0).equalsIgnoreCase("f")){
            FrameBody frameBody = new FrameBody(Grade.FACTORY);
//            EnergyCore energyCore = new EnergyCore(frameBody, Rarity.MASS, Grade.SHATTERED, Tier.I);

            ChargeCell cg = new ChargeCell(frameBody, Rarity.MASS, Grade.SHATTERED, Tier.I);

            frameBody.addChargeCell(cg);
//            frameBody.addEnergyCore(energyCore);
            frameBody.tickFrameBody(frameBody);
            frameBody.debug();
            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().put(frameBody.getFrameUUID(), frameBody);

        }
        if (args.getArgs(0).equalsIgnoreCase("tick")) {
            ;
            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().forEach((uuid, frameBody) -> frameBody.tickFrameBody(frameBody));
            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().forEach((uuid, frameBody) -> frameBody.debug());
        }
        if (args.getArgs(0).equalsIgnoreCase("a")) {

            EnergyCore energyCore = new EnergyCore(Rarity.PRIME, Grade.FACTORY, Tier.II);

            EnergyWeaponBuilder.builder(Grade.PRIMED).setEnergyCore(new EnergyCore(Rarity.BLACKMARKET, Grade.PRIMED, Tier.II)).build(true);
        }

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

        if (args.getArgs(0).equalsIgnoreCase("rf")) {
            ItemStack itemstack = player.getInventory().getItemInMainHand();
//            if (itemstack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Settlements.getInstance(), WeaponComponent.getInstance().getWeaponKey()), WeaponComponent.getInstance().getFrameBodyDataType())
            String uuid = NBT.get(itemstack, (Function<ReadableItemNBT, String>) nbt -> nbt.getString("uuid"));
            if (uuid != null) player.sendMessage(uuid);
            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().forEach((uuidd, fb) -> System.out.println(uuid + " : " + fb.getFrameUUID()));
        }
            if (args.getArgs(0).equalsIgnoreCase("r")) {

            FrameBody fb = WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().get(UUID.fromString(NBT.readNbt(player.getInventory().getItemInMainHand()).getString("uuid")));;
            EnergyCore energyCore = new EnergyCore(Rarity.PRIME, Grade.PRIMED, Tier.II);
            fb.getChargeCell().chargeCore();
            fb.debug();
            energyCore.onTick();
            energyCore.expendEnergy();
            energyCore.onTick();
            fb.debug();
            args.getSender().sendMessage("simulation complete of :" + fb.getFrameUUID());
            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().put(fb.getFrameUUID(), fb);
        }


    }
}
