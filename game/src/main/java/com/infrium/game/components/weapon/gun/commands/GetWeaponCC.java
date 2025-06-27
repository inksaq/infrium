package com.infrium.game.components.weapon.gun.commands;

import com.infrium.core.api.command.Command;
import com.infrium.core.api.command.CommandArgs;
import com.infrium.game.BaseCommand;
import com.infrium.game.Settlements;
import com.infrium.game.components.weapon.WeaponComponent;
import com.infrium.game.components.weapon.energy.components.core.CoreComponent;
import com.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import com.infrium.game.components.weapon.energy.components.core.components.*;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import com.infrium.game.components.weapon.energy.components.core.components.upgrades.chargecell.FastCharge;
import com.infrium.game.components.weapon.gun.armory.SCAR90;
import com.infrium.game.item.Grade;
import com.infrium.game.item.Rarity;
import com.infrium.game.item.Tier;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GetWeaponCC extends BaseCommand {

    @Command(name = "wp", description = "weapon commands")
    public void onCommand(CommandArgs args) {
        Player player = (Player) args.getSender();
        if (args.length() == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /wp <scar|fb|f|devchest|workstation>");
            return;
        }

        switch (args.getArgs(0).toLowerCase()) {
            case "scar":
                giveScar(player);
                break;
            case "fb":
                giveFrameBody(player);
                break;
            case "f":
                giveSimpleFrameBody(player);
                break;
            case "devchest":
                handleDevChestCommand(player, args);
                break;
            case "workstation":
                WeaponComponent.getInstance().getWeaponDevelopmentSystem().openWorkstation(player);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown weapon command. Use scar, fb, f, devchest, or workstation.");
        }
    }

    private void handleDevChestCommand(Player player, CommandArgs args) {
        if (args.length() < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /wp devchest <open|add|remove>");
            return;
        }

        switch (args.getArgs(1).toLowerCase()) {
            case "open":
                WeaponComponent.getInstance().getWeaponDevelopmentSystem().openDevChest(player);
                break;
            case "add":
                ItemStack heldItem = player.getInventory().getItemInMainHand();
                if (heldItem.getType() != Material.AIR) {
                    WeaponComponent.getInstance().getWeaponDevelopmentSystem().addItemToDevChest(player, heldItem.clone());
                    player.getInventory().setItemInMainHand(null);
                    player.sendMessage(ChatColor.GREEN + "Added held item to your Dev Chest.");
                } else {
                    player.sendMessage(ChatColor.RED + "You must be holding an item to add to the Dev Chest.");
                }
                break;
            case "remove":
                if (args.length() < 3) {
                    player.sendMessage(ChatColor.RED + "Usage: /wp devchest remove <index>");
                    return;
                }
                try {
                    int index = Integer.parseInt(args.getArgs(2));
                    WeaponComponent.getInstance().getWeaponDevelopmentSystem().removeItemFromDevChest(player, index);
                    player.sendMessage(ChatColor.GREEN + "Removed item from your Dev Chest.");
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid index. Please provide a number.");
                }
                break;
            default:
                player.sendMessage(ChatColor.RED + "Unknown Dev Chest command. Use open, add, or remove.");
        }
    }


    private void giveScar(Player player) {
        player.getInventory().addItem(new SCAR90("scar90").getItem());
        player.sendMessage(ChatColor.GREEN + "You have received a SCAR90!");
    }

    private void giveFrameBody(Player player) {
        try {
            FrameBody frameBody = new FrameBody(Grade.FACTORY, Rarity.BLACKMARKET, Tier.IV);
            EnergyCore energyCore = new EnergyCore(frameBody, Rarity.PRIME, Grade.FACTORY, Tier.II);
            ChargeCell chargeCell = new ChargeCell(frameBody, Rarity.PRIME, Grade.PRIMED, Tier.IV);
            CoreProcessor coreProcessor = new CoreProcessor(frameBody, Rarity.PRIME, Grade.PRIMED, Tier.IV);
            LensConduit lensConduit = new LensConduit(frameBody, Rarity.PRIME, Grade.PRIMED, Tier.IV);

            frameBody.addComponent(energyCore);
            frameBody.addComponent(chargeCell);
            frameBody.addComponent(coreProcessor);
            frameBody.addComponent(lensConduit);

            ComponentUpgrade<?> fastChargeUpgrade = new FastCharge(Rarity.BLACKMARKET, Grade.PRIMED, Tier.IV);

            CoreComponent chargeCellComponent = frameBody.getComponent(CoreComponentType.CHARGE_CELL);
            if (chargeCellComponent != null) {
                chargeCellComponent.addUpgrade(fastChargeUpgrade);
            } else {
                Settlements.getInstance().getLogger().warning("Charge Cell component not found in Frame Body.");
                player.sendMessage(ChatColor.RED + "Error adding upgrade to Charge Cell.");
            }

            frameBody.onTick();

            ItemStack weaponItem = frameBody.createItemStack();
            WeaponComponent.getInstance().getWeaponRegistry().registerWeapon(frameBody);
            player.getInventory().addItem(weaponItem);

            player.sendMessage(ChatColor.GREEN + "You have received a new Frame Body weapon!");
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "An error occurred while creating the weapon.");
        }
    }

    private void giveSimpleFrameBody(Player player) {
        try {
            FrameBody frameBody = new FrameBody(Grade.FACTORY, Rarity.BLACKMARKET, Tier.II);
            ChargeCell chargeCell = new ChargeCell(Rarity.MASS, Grade.SHATTERED, Tier.I);

            frameBody.addComponent(chargeCell);
            frameBody.onTick();

            player.sendMessage(ChatColor.YELLOW + "Frame Body Debug Information:");
            for (String line : frameBody.getFrameBodyLore()) {
                player.sendMessage(line);
            }

            WeaponComponent.getInstance().getWeaponRegistry().registerWeapon(frameBody);

            ItemStack frameBodyItem = frameBody.createItemStack();
            player.getInventory().addItem(frameBodyItem);

            player.sendMessage(ChatColor.GREEN + "You have received a new Frame Body!");
        } catch (Exception e) {
            Settlements.getInstance().getLogger().severe("Error creating simple Frame Body: " + e.getMessage());
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + "An error occurred while creating the Frame Body.");
        }
    }
}
//        if (args.getArgs(0).equalsIgnoreCase("tick")) {
//            ;
//            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().forEach((uuid, frameBody) -> frameBody.tickFrameBody());
//            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().forEach((uuid, frameBody) -> frameBody.debug());
//        }
//        if (args.getArgs(0).equalsIgnoreCase("a")) {
//
//            EnergyCore energyCore = new EnergyCore(Rarity.PRIME, Grade.FACTORY, Tier.II);
//
//            EnergyWeaponBuilder.builder().setEnergyCore(energyCore).build(true);
//        }

//        if (args.getArgs(0).equalsIgnoreCase("ra")) {
//            ItemStack mainHand = player.getInventory().getItemInMainHand();
//            if (WeaponComponent.getInstance().getWeaponRegistry().getFrameBody(mainHand) != null){
//                var framebody = WeaponComponent.getInstance().getWeaponRegistry().getFrameBody(mainHand);
//                framebody.tickFrameBody(mainHand, framebody);
//                framebody.debug();
//                if (framebody.isOperational()) {
//                    player.sendMessage("not operational");
//                }
//            }
//
//        }
//        if (args.getArgs(0).equalsIgnoreCase("rb")) {
//            WeaponRegistry.getInstance().getFramebodies().forEach((uuid, frameBody) -> {System.out.println(uuid.toString() + "\n");
//            frameBody.debug();});
//        }
//        if (args.getArgs(0).equalsIgnoreCase("rc")) {
//            var gui = new WorkbenchGUI(LegacyComponentSerializer.legacyAmpersand().deserialize("Weapon"), Settlements.getInstance());
//            gui.openInventory(player);
//        }
//
//        if (args.getArgs(0).equalsIgnoreCase("rl")) {
//            ItemStack itemstack = player.getInventory().getItemInMainHand();
//            Set<String> keys = NBT.get(itemstack, ReadableItemNBT::getKeys);
//            for (String key : keys) {
//                String value = NBT.get(itemstack, (Function<ReadableItemNBT, String>) nbt -> nbt.getString(key));
//                player.sendMessage(key + ": " + value);
//            }        }
//
//        if (args.getArgs(0).equalsIgnoreCase("rf")) {
//            ItemStack itemstack = player.getInventory().getItemInMainHand();
////            if (itemstack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(Settlements.getInstance(), WeaponComponent.getInstance().getWeaponKey()), WeaponComponent.getInstance().getFrameBodyDataType())
//            String uuid = NBT.get(itemstack, (Function<ReadableItemNBT, String>) nbt -> nbt.getString("uuid"));
//            if (uuid != null) player.sendMessage(uuid);
//            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().forEach((uuidd, fb) -> System.out.println(uuid + " : " + fb.getFrameUUID()));
//        }
//            if (args.getArgs(0).equalsIgnoreCase("r")) {
//            ItemStack itemstack = player.getInventory().getItemInMainHand();
//            String uuid = NBT.get(itemstack, (Function<ReadableItemNBT, String>) nbt -> nbt.getString("uuid"));
//                var fb = WeaponComponent.getInstance().getWeaponRegistry().getFrameBody(itemstack);
////            FrameBody fb = WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().get(uuid).getFrameBody();
//
//            EnergyCore energyCore = fb.getEnergyCore();
//            ChargeCell chargeCell = fb.getChargeCell();
////            fb.debug();
//            chargeCell.chargeCore();
//            energyCore.onTick();
//            energyCore.expendEnergy();
//            energyCore.onTick();
////            fb.debug();
//            args.getSender().sendMessage("simulation complete of :" + fb.getFrameUUID());
//            WeaponComponent.getInstance().getWeaponRegistry().getFramebodies().put(fb.getFrameUUID(), fb);
//        }
//
//
//    }
//}
