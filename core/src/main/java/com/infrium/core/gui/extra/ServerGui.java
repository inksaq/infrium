package com.infrium.core.gui.extra;

import com.infrium.api.hive.enums.ServerType;
import com.infrium.api.hive.servers.Server;
import com.infrium.core.Teleporter;
import com.infrium.core.events.OnServerAddEvent;
import com.infrium.core.events.OnServerDeleteEvent;
import com.infrium.core.events.OnServerUpdateEvent;
import com.infrium.core.events.OnSyncEvent;
import com.infrium.core.gui.AbstractGui;
import com.infrium.core.item.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

import static com.infrium.api.util.LangUtils.listOf;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand;

public class ServerGui extends AbstractGui {

  private final Inventory inventory;
  private final ServerType serverType;

  public ServerGui(ServerType serverType, Component title, JavaPlugin plugin) {
    super(54, title, plugin);
    this.serverType = serverType;
    this.inventory = Bukkit.createInventory(null, this.size, this.title);
    rebuildUI();
  }

  private void rebuildUI() {
    this.inventory.clear();
    this.fill(inventory, ItemBuilder.builder().setMaterial(Material.LIGHT_GRAY_STAINED_GLASS_PANE).setName(Component.empty()).build(false), 0, this.size);

    var servers =
        new ArrayList<>(
            com.infrium.core.ICore.getInstance()
                .getInfriumProvider()
                .getRepository()
                .getServers(this.serverType));
    for (Server server : servers) {
      try {
        int i = Integer.parseInt(server.getName().split("-")[1]);
        inventory.setItem(i, craftItem(server));
      } catch (Exception e) {
        // ignored exception
      }
    }
  }

  public void openInventory(Player player) {
    player.openInventory(this.inventory);
  }

  @Override
  protected void onItemClick(Player player, ItemStack clickedItem, Inventory inventory) {
    if (clickedItem.getType().name().contains("WOOL") && clickedItem.getType() != Material.RED_WOOL) {
      String itemName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName().split(" ")[0]);
      Teleporter.connect(player.getName(), itemName);
    } else {
      player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 5, -20);
    }
  }

  public ItemStack craftItem(Server server) {
    var mat =
        (server.getServerStatus().getOnlinePlayers() >= server.getServerType().getMaxPlayers() / 2
            ? Material.ORANGE_WOOL
            : Material.GREEN_WOOL);

    var dn = (server.getServerStatus().getOnlinePlayers() >= server.getServerType().getMaxPlayers() / 2
            ? LegacyComponentSerializer.legacySection()
            .deserialize(
                    ChatColor.YELLOW
                            + StringUtils.capitalize(server.getName()) + " &7" + server.getServerStatus().getOnlinePlayers() + "&8/&7" + server.getServerType().getMaxPlayers())
            : LegacyComponentSerializer.legacySection()
            .deserialize(
                    ChatColor.GREEN
                            + StringUtils.capitalize(server.getName()) + " &7" + server.getServerStatus().getOnlinePlayers() + "&8/&7" + server.getServerType().getMaxPlayers()));

    var empty = LegacyComponentSerializer.legacySection().deserialize("");

    return ItemBuilder.builder()
        .setMaterial(mat)
        .setSize(1)
        .setName(dn)
        .setLore(
            listOf(
                empty,
                legacyAmpersand()
                    .deserialize(
                        "&7Click to join this shard"),
                empty))
        .build(false);
  }

  @EventHandler
  public void onServerAdd(OnServerAddEvent event) {
    if (event.getServer().getServerType().equals(this.serverType)) {
      try {
        int i = Integer.parseInt(event.getServer().getName().split("-")[1]);
        inventory.setItem(i, craftItem(event.getServer()));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @EventHandler
  public void onServerDelete(OnServerDeleteEvent event) {
    if (event.getServer().getServerType().equals(this.serverType)) {
      try {
        int i = Integer.parseInt(event.getServer().getName().split("-")[1]);
        inventory.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @EventHandler
  public void onSync(OnSyncEvent event) {
    this.rebuildUI();
  }

  @EventHandler
  public void onServerUpdate(OnServerUpdateEvent event) {
    try {
      int i = Integer.parseInt(event.getServer().getName().split("-")[1]);
      inventory.setItem(i, craftItem(event.getServer()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
