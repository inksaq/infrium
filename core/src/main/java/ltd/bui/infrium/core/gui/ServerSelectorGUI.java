package ltd.bui.infrium.core.gui;


import ltd.bui.infrium.api.hive.enums.ServerType;
import ltd.bui.infrium.api.hive.servers.Server;
import ltd.bui.infrium.core.InfriumCore;
import ltd.bui.infrium.core.Teleporter;
import ltd.bui.infrium.core.events.OnServerAddEvent;
import ltd.bui.infrium.core.events.OnServerDeleteEvent;
import ltd.bui.infrium.core.events.OnServerUpdateEvent;
import ltd.bui.infrium.core.events.OnSyncEvent;
import ltd.bui.infrium.core.item.ItemBuilder;
import ltd.bui.infrium.core.player.BukkitInfriumPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ltd.bui.infrium.api.hive.enums.ServerType.LOBBY;
import static ltd.bui.infrium.api.util.LangUtils.listOf;
import static net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand;

public class ServerSelectorGUI extends AbstractGui {
  private final Inventory inventory;

  public ServerSelectorGUI(Component title, JavaPlugin plugin) {
      super(9, title, plugin);
      this.inventory = Bukkit.createInventory(null, this.size, this.title);
      rebuildUI();
    }

  private void rebuildUI() {
    this.inventory.clear();

    this.fill(inventory, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1), 0, this.size);
    var servers =
            new ArrayList<>(
                    InfriumCore.getInstance()
                            .getInfriumProvider()
                            .getRepository()
                            .getServers().stream().filter(server -> !server.getServerType().equals(LOBBY)).toList());
    for (Server server : servers) {
      try {
        int i = servers.indexOf(server);
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
      String serverName = MiniMessage.miniMessage().escapeTags(itemName);
      if ((Objects.equals(serverName, InfriumCore.getInstance().getServerName()))) {
        InfriumCore.getInstance().getInfriumProvider().craftInfriumPlayer(player).sendTitle(
                legacyAmpersand().deserialize("&cAlready on this shard"),
                legacyAmpersand().deserialize("&7You are already on this shard"),
                10,
                40,
                10);
//        player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
//                .deserialize("&cYou are already on this shard."));
        return;
      }
      Teleporter.connect(serverName, player.getName());
    }
  }

  public ItemStack craftItem(Server server) {
    var mat =
            (server.getServerStatus().getOnlinePlayers() >= server.getServerType().getMaxPlayers() / 2
                    ? Material.ORANGE_WOOL
                    :  (Objects.equals(server.getName(), InfriumCore.getInstance().getServerName()) ? Material.GREEN_WOOL : Material.WHITE_WOOL));
    if (!server.getServerType().canJoin(server)) mat = Material.RED_WOOL;

    var dn = (server.getServerStatus().getOnlinePlayers() >= server.getServerType().getMaxPlayers() / 2
            ? LegacyComponentSerializer.legacyAmpersand()
            .deserialize(
                    ChatColor.YELLOW
                            + StringUtils.capitalize(server.getName()) + " &7" + server.getServerStatus().getOnlinePlayers() + "&8/&7" + server.getServerType().getMaxPlayers())
            : LegacyComponentSerializer.legacyAmpersand()
            .deserialize(
                    ChatColor.GREEN
                            + StringUtils.capitalize(server.getName()) + " &7" + server.getServerStatus().getOnlinePlayers() + "&8/&7" + server.getServerType().getMaxPlayers()));

    var empty = LegacyComponentSerializer.legacySection().deserialize("");

    return ItemBuilder.builder()
            .setMaterial(mat)
            .setSize(1)
            .setName(dn)
            .setLore(
                    (server.getServerType().canJoin(server)) ?
                    listOf(empty,
                            (Objects.equals(server.getName(), InfriumCore.getInstance().getServerName()) ?
                                    legacyAmpersand().deserialize("&7Already on this shard") :
                                    legacyAmpersand().deserialize("&7Click to join this shard")),
                            empty) :
                            listOf(empty,
                                    legacyAmpersand().deserialize("&cRestricted Access"),
                                    empty))
            .build((Objects.equals(server.getName(), InfriumCore.getInstance().getServerName())));
  }

  @EventHandler
  public void onServerAdd(OnServerAddEvent event) {
    if (event.getServer().getServerType() != LOBBY) {
      try {
        rebuildUI();
//        int i = Integer.parseInt(event.getServer().getName().split("-")[1]);
//        inventory.setItem(, craftItem(event.getServer()));
//        ai++;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @EventHandler
  public void onServerDelete(OnServerDeleteEvent event) {
    if (event.getServer().getServerType() != LOBBY) {
      try {
        rebuildUI();
//        int i = Integer.parseInt(event.getServer().getName().split("-")[1]);
//        inventory.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
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
    if (event.getServer().getServerType() != LOBBY)
    try {
      rebuildUI();
//      int i = Integer.parseInt(event.getServer().getName().split("-")[1]);
//      inventory.setItem(i, craftItem(event.getServer()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
