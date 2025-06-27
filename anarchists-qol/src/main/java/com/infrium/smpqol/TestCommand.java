package com.infrium.smpqol;

import com.infrium.api.data.Rank;
import com.infrium.core.api.command.Command;
import com.infrium.core.api.command.CommandArgs;
import com.infrium.core.api.command.Completer;
import com.infrium.smpqol.item.QolItemSystem;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class TestCommand extends BaseCommand implements Listener {
    private final JavaPlugin plugin;
    private final QolItemSystem qolSystem;


    public TestCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        this.qolSystem = new QolItemSystem(plugin);

        Bukkit.getServer().getPluginManager().registerEvents(
                this, plugin
        );
    }

    @Command(name = "test", description = "Test command for quick development")
    public void onTest(CommandArgs args) {
        Player player = args.getPlayer();

        if (args.length() == 0) {
            sendHelpMessage(player);
            return;
        }

        switch (args.getArgs(0).toLowerCase()) {
            case "energyweapon" -> giveEnergyWeapon(player);
            case "checkitem" -> checkHeldItem(player);
            case "ranks" -> listRanks(args);
            default -> sendHelpMessage(player);
        }
    }

    private void giveEnergyWeapon(Player player) {
        // Example energy weapon state implementation
        class EnergyWeaponState implements QolItemSystem.ItemState {
            private double energy = 100.0;
            private final double maxEnergy = 100.0;
            private final double rechargeRate = 5.0;
            private boolean needsUpdate = false;

            @Override
            public String getType() {
                return "energy_weapon";
            }

            @Override
            public boolean needsUpdate() {
                return needsUpdate;
            }

            @Override
            public void update(ItemStack item) {
                if (item == null) return;

                NBTItem nbtItem = new NBTItem(item);
                var weaponData = nbtItem.addCompound("InfriumData");
                weaponData.setString("type", "energy_weapon");
                weaponData.setDouble("energy", energy);
                weaponData.setDouble("maxEnergy", maxEnergy);
                weaponData.setDouble("rechargeRate", rechargeRate);

                nbtItem.applyNBT(item);
                needsUpdate = false;
            }

            public void consumeEnergy(double amount) {
                energy = Math.max(0, Math.min(maxEnergy, energy - amount));
                needsUpdate = true;
            }
        }

        // Create the energy weapon
        ItemStack energyWeapon = qolSystem.builder(Material.DIAMOND_SWORD)
                .name(Component.text("Energy Sword").color(NamedTextColor.AQUA))
                .type("energy_weapon")
                .creator(player.getUniqueId().toString())
                .state(new EnergyWeaponState())
                .build();

        // Give to player
        player.getInventory().addItem(energyWeapon);
        player.sendMessage(Component.text("You received an Energy Sword!").color(NamedTextColor.GREEN));
    }

    private void checkHeldItem(Player player) {
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (qolSystem.isQolItem(heldItem)) {
            qolSystem.getItemMetadata(heldItem).ifPresent(metadata -> {
                player.sendMessage(Component.text("=== Item Information ===").color(NamedTextColor.GOLD));
                player.sendMessage(Component.text("Type: " + metadata.type()).color(NamedTextColor.YELLOW));
                player.sendMessage(Component.text("Creator: " + metadata.creator()).color(NamedTextColor.YELLOW));
                player.sendMessage(Component.text("Created: " + new java.util.Date(metadata.creationTime()))
                        .color(NamedTextColor.YELLOW));

                // If it's an energy weapon, show energy information
                NBTItem nbtItem = new NBTItem(heldItem);
                if (nbtItem.hasKey("InfriumData")) {
                    var weaponData = nbtItem.getCompound("InfriumData");
                    if ("energy_weapon".equals(weaponData.getString("type"))) {
                        double energy = weaponData.getDouble("energy");
                        double maxEnergy = weaponData.getDouble("maxEnergy");
                        player.sendMessage(Component.text(String.format("Energy: %.1f/%.1f", energy, maxEnergy))
                                .color(NamedTextColor.AQUA));
                    }
                }
            });
        } else {
            player.sendMessage(Component.text("This is not a QoL item!").color(NamedTextColor.RED));
        }
    }

    @Command(name = "ranks", description = "List all ranks")
    public void listRanks(CommandArgs args) {
        Arrays.stream(Rank.values()).iterator().forEachRemaining(rank ->
                args.getPlayer().sendMessage(rank.getName() + " " + rank.getPrefix()));
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(Component.text("=== Test Command Help ===").color(NamedTextColor.GOLD));
        player.sendMessage(Component.text("/test energyweapon").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Get an energy weapon").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/test checkitem").color(NamedTextColor.YELLOW)
                .append(Component.text(" - Check held item information").color(NamedTextColor.GRAY)));
        player.sendMessage(Component.text("/test ranks").color(NamedTextColor.YELLOW)
                .append(Component.text(" - List all ranks").color(NamedTextColor.GRAY)));
    }
}