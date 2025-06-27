package com.infrium.lobby.listener;

import com.infrium.api.data.InfriumPlayerData;
import com.infrium.api.player.AbstractInfriumPlayer;
import com.infrium.lobby.InfriumLobby;
import com.infrium.lobby.Items;
import com.infrium.lobby.configuration.LobbyConfiguration;
import com.infrium.lobby.cosmetic.Cosmetics;
import org.bukkit.*;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Objects;

public class PlayerListener implements Listener {

    ArrayList<Player> waitingToFly = new ArrayList<Player>();

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE)
            return;


        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(1));
        player.setExp(0.00F);
        waitingToFly.add(player);
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_INFECT, 1, 2);
        return;




    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        BukkitScheduler scheduler = InfriumLobby.getInstance().getServer().getScheduler();
        Player player = event.getPlayer();

        if ((player.getGameMode() == GameMode.CREATIVE))
            return;
        if (player.isFlying())
            return;
        if (!(player.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()))
            return;
        if (player.getExp() > 0.00F)
            return;

        if (waitingToFly.contains(player)) {

            scheduler.scheduleSyncDelayedTask(InfriumLobby.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (player.getLocation().subtract(0, 1, 0).getBlock().getType().isSolid()) {
                        player.setExp(0.99F);
                        player.setAllowFlight(true);
                        waitingToFly.remove(player);
                    }
                }
            }, 7L);
        } else {
            player.setExp(0.99F);
            player.setAllowFlight(true);
        }

    }

    @EventHandler
    public final void onJoin(PlayerJoinEvent event) {
        event.getPlayer().teleport(InfriumLobby.getInstance().getLobbyLocation());
        Items.formatInventory(event.getPlayer());
        AbstractInfriumPlayer<Player> gp = com.infrium.core.ICore.getInstance().getInfriumProvider().getInfriumPlayer(event.getPlayer()).get();
        InfriumPlayerData data = gp.getPlayerData();
        //Load cosmetics
        InfriumLobby.getInstance().getCosmeticsManager().unlockCosmetic(event.getPlayer(),
                InfriumLobby.getInstance().getCosmeticsManager().getCosmeticByName(Cosmetics.MIDAS_TOUCH.getName()));
        InfriumLobby.getInstance().getCosmeticsManager().unlockCosmetic(event.getPlayer(),
                InfriumLobby.getInstance().getCosmeticsManager().getCosmeticByName(Cosmetics.PARTICLE_WINGS.getName()));
        InfriumLobby.getInstance().getCosmeticsManager().unlockCosmetic(event.getPlayer(),
                InfriumLobby.getInstance().getCosmeticsManager().getCosmeticByName(Cosmetics.HALO.getName()));
    }

    @EventHandler
    public final void onQuit(PlayerQuitEvent event) {
        InfriumLobby.getInstance().getBuildingPlayers().remove(event.getPlayer());
    }

    @EventHandler()
    public void onPlayerUse(final PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if (InfriumLobby.getInstance().getGamesManager().isPlayerPlaying(p) || InfriumLobby.getInstance().getBuildingPlayers().contains(p)) {
            return;
        }

        switch (p.getInventory().getItemInMainHand().getType()) {
            case COMPASS -> {
                if (event.getAction().equals(Action.PHYSICAL)) return;
                com.infrium.core.ICore.getInstance().getServerSelectorGUI().openInventory(p);
            }
            case CLOCK -> {
                if (event.getAction().equals(Action.PHYSICAL)) return;
                InfriumLobby.getInstance().getLobbySelectorGUI().openInventory(p);
            }
            case CHEST -> {
                if (event.getAction().equals(Action.PHYSICAL)) return;
                InfriumLobby.getInstance().getCosmeticSelectorGUI().openInventory(p);

            }
            default -> {
                // do nothing
            }
        }
    }


    @EventHandler
    public final void onInvClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        if (InfriumLobby.getInstance().getGamesManager().isPlayerPlaying(player) || InfriumLobby.getInstance().getBuildingPlayers().contains(player))
            return;
        if (event.getClickedInventory() == null) return;

        if (event.getClickedInventory().getType() == InventoryType.CRAFTING || event.getClickedInventory().getType() == InventoryType.WORKBENCH || event.getClickedInventory().getType() == InventoryType.PLAYER) {
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler
    public final void disableSecondHandSwap(PlayerSwapHandItemsEvent event) {
        if (InfriumLobby.getInstance().getGamesManager().isPlayerPlaying(event.getPlayer()) || InfriumLobby.getInstance().getBuildingPlayers().contains(event.getPlayer()))
            return;
        event.setCancelled(true);
    }

    @EventHandler
    public final void onItemDrop(PlayerDropItemEvent event) {
        var f1 = !InfriumLobby.getInstance().getGamesManager().isPlayerPlaying(event.getPlayer());
        var f2 = !InfriumLobby.getInstance().getBuildingPlayers().contains(event.getPlayer());
        if (f1 && f2) event.setCancelled(true);
    }

    @EventHandler
    public final void disablePlayerDamage(final EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!InfriumLobby.getInstance().getGamesManager().isPlayerPlaying(player)) {
            event.setCancelled(true);
            player.closeInventory(InventoryCloseEvent.Reason.CANT_USE);
            (player).updateInventory();
        }
    }

    @EventHandler
    public final void onPlayerDamaged(final EntityDamageEvent event) {
        if (event.getEntity() instanceof ItemFrame) {
            event.setCancelled(true);
            event.setDamage(0);
            return;
        }

        if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION
                || event.getCause() == EntityDamageEvent.DamageCause.DROWNING
                || event.getCause() == EntityDamageEvent.DamageCause.FIRE
                || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || event.getCause() == EntityDamageEvent.DamageCause.LAVA
                || event.getCause() == EntityDamageEvent.DamageCause.WITHER
                || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                || event.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
            event.setCancelled(true);
            event.setDamage(0);
            event.getEntity().setFireTicks(0);
            return;
        }

        if (!(event.getEntity() instanceof Player player)) return;
        if (!InfriumLobby.getInstance().getGamesManager().isPlayerPlaying(player)) {
            event.setCancelled(true);
            event.setDamage(0);
            player.closeInventory(InventoryCloseEvent.Reason.CANT_USE);
            (player).updateInventory();
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK && (Objects.requireNonNull(event.getClickedBlock()).getType().toString().toLowerCase().contains("chest") || event.getClickedBlock().getType().toString().toLowerCase().contains("door")
                || event.getClickedBlock().getType().toString().toLowerCase().contains("fence")
                || event.getClickedBlock().getType().equals(Material.DISPENSER)
                || event.getClickedBlock().getType().equals(Material.DROPPER)
                || event.getClickedBlock().getType().equals(Material.HOPPER)
                || event.getClickedBlock().getType().equals(Material.BEACON)
                || event.getClickedBlock().getType().equals(Material.ANVIL)
                || event.getClickedBlock().getType().equals(Material.ENCHANTING_TABLE)
                || event.getClickedBlock().getType().equals(Material.CRAFTING_TABLE)) && !InfriumLobby.getInstance().getBuildingPlayers().contains(event.getPlayer())) {
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void disableCraft(CraftItemEvent event) {
        event.setCancelled(true);
    }

    // WORLD BEHAVIOR


    @EventHandler
    public final void onBlockPlace(BlockPlaceEvent event) {
        if (!InfriumLobby.getInstance().getGamesManager().isPlayerPlaying(event.getPlayer()) && !InfriumLobby.getInstance().getBuildingPlayers().contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public final void onBlockBreak(BlockBreakEvent event) {
        if (!InfriumLobby.getInstance().getGamesManager().isPlayerPlaying(event.getPlayer()) && !InfriumLobby.getInstance().getBuildingPlayers().contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFallingBlockLand(final EntityChangeBlockEvent e) {
        if (e.getEntity() instanceof FallingBlock) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onGrassBecomeDirt(final BlockPhysicsEvent event) {
        if (event.getChangedType().equals(Material.GRASS_BLOCK) && event.getBlock().getType().equals(Material.DIRT)) {
            event.setCancelled(true);
            event.getBlock().getState().update();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWeatherChange(final WeatherChangeEvent event) {
        if (!event.toWeatherState()) {
            return;
        }
        event.setCancelled(true);
        event.getWorld().setWeatherDuration(0);
        event.getWorld().setThundering(false);
    }

    @EventHandler //evita di mettere item dentro itemframe
    public void itemFrameCheck2(final PlayerInteractEntityEvent e) {
        if (e.getRightClicked() instanceof ItemFrame && !InfriumLobby.getInstance().getBuildingPlayers().contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onHungerDeplete(FoodLevelChangeEvent e) {
        e.setCancelled(true);
        e.setFoodLevel(20);
    }


}
