package ltd.bui.infrium.lobby.game.s;

import ltd.bui.infrium.core.helpers.MessageUtils;
import ltd.bui.infrium.lobby.InfriumLobby;
import ltd.bui.infrium.lobby.game.LobbyGame;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class FFALobbyGame extends LobbyGame {

  private static final ItemStack[] items =
      new ItemStack[] {
        new ItemStack(Material.NETHERITE_HELMET),
        new ItemStack(Material.NETHERITE_CHESTPLATE),
        new ItemStack(Material.NETHERITE_LEGGINGS),
        new ItemStack(Material.NETHERITE_BOOTS),
        new ItemStack(Material.NETHERITE_SWORD),
        new ItemStack(Material.REDSTONE_TORCH),
      };

  static {
    for (var item : items) {
      var meta = item.getItemMeta();
      meta.setUnbreakable(true);
      item.setItemMeta(meta);
    }
    var meta = items[5].getItemMeta();
    meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("&c&lPVP Disabler"));
    items[5].setItemMeta(meta);
  }

  @Override
  public void join(Player player) {
    this.playerList.add(player);
    player.getInventory().clear();
    // give items
    player.getInventory().setItem(0, items[4]);
    player.getInventory().setItem(8, items[5]);
    player.getInventory().setHelmet(items[0]);
    player.getInventory().setChestplate(items[1]);
    player.getInventory().setLeggings(items[2]);
    player.getInventory().setBoots(items[3]);
    player.getInventory().setHeldItemSlot(0);
  }

  @EventHandler
  public void onHandChange(PlayerItemHeldEvent event) {
    var held = event.getPlayer().getInventory().getItem(event.getNewSlot());
    if (held != null
        && held.getItemMeta() != null
        && held.getItemMeta().displayName() instanceof TextComponent) {
      var component = (TextComponent) held.getItemMeta().displayName();
      if (component.content().equals("PVP Enabler") && !this.isPlayerPlaying(event.getPlayer())) {
        new BukkitRunnable() {
          private final long start = System.currentTimeMillis();

          @Override
          public void run() {
            if (event.getPlayer().isOnline()) {
              var held = event.getPlayer().getInventory().getItemInMainHand();
              if (held.getItemMeta() != null
                  && held.getItemMeta().displayName() instanceof TextComponent) {
                var component = (TextComponent) held.getItemMeta().displayName();
                if (component.content().equals("PVP Enabler")
                    && !isPlayerPlaying(event.getPlayer())) {
                  int seconds = (int) ((System.currentTimeMillis() - start) / 1000);
                  int percentage = (seconds * 100) / 5;
                  MessageUtils.sendActionbarPercentage(
                      "&a&lPVP ",
                      " &a" + (5 - seconds) + "s",
                      ChatColor.GREEN,
                      ChatColor.DARK_GRAY,
                      percentage,
                      event.getPlayer());
                  if (System.currentTimeMillis() - start >= 5000) {
                    MessageUtils.sendActionbar("&6&lPVP ENABLED", event.getPlayer());
                    join(event.getPlayer());
                    this.cancel();
                  }
                } else {
                  this.cancel();
                }
              } else {
                this.cancel();
              }
            } else {
              this.cancel();
            }
          }
        }.runTaskTimer(InfriumLobby.getInstance(), 0, 10);
      } else if (component.content().equals("PVP Disabler")
          && this.isPlayerPlaying(event.getPlayer())) {
        new BukkitRunnable() {
          private final long start = System.currentTimeMillis();

          @Override
          public void run() {
            if (event.getPlayer().isOnline()) {
              var held = event.getPlayer().getInventory().getItemInMainHand();
              if (held.getItemMeta() != null
                  && held.getItemMeta().displayName() instanceof TextComponent) {
                var component = (TextComponent) held.getItemMeta().displayName();
                if (component.content().equals("PVP Disabler")
                    && isPlayerPlaying(event.getPlayer())) {
                  int seconds = (int) ((System.currentTimeMillis() - start) / 1000);
                  int percentage = (seconds * 100) / 5;
                  MessageUtils.sendActionbarPercentage(
                      "&c&lPVP ",
                      " &c" + (5 - seconds) + "s",
                      ChatColor.RED,
                      ChatColor.DARK_GRAY,
                      percentage,
                      event.getPlayer());
                  if (System.currentTimeMillis() - start >= 5000) {
                    MessageUtils.sendActionbar("&c&lPVP DISABLED", event.getPlayer());
                    event.getPlayer().closeInventory();
                    quit(event.getPlayer());
                    this.cancel();
                  }
                } else {
                  this.cancel();
                }
              } else {
                this.cancel();
              }
            } else {
              this.cancel();
            }
          }
        }.runTaskTimer(InfriumLobby.getInstance(), 0, 10);
      }
    }
  }

  @EventHandler
  public void onInvClick(InventoryClickEvent event) {
    if (event.getWhoClicked() instanceof Player
        && isPlayerPlaying((Player) event.getWhoClicked())) {
      event.setCancelled(true);
      event.getWhoClicked().closeInventory(InventoryCloseEvent.Reason.CANT_USE);
      ((Player) event.getWhoClicked()).updateInventory();
    }
  }

  @EventHandler
  public void onItemDrop(PlayerDropItemEvent event) {
    if (isPlayerPlaying(event.getPlayer())) {
      event.setCancelled(true);
      event.getPlayer().updateInventory();
    }
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    event.setCancelled(true);
  }
}
