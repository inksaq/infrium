package com.infrium.game.components.testing.listeners;

import com.infrium.game.Settlements;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TestListener implements Listener {

    public TestListener(Settlements plugin) {

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
    }

/*    @EventHandler
    public void onNPCInteract(NPCInteractEvent event) {
            event.getWhoClicked().sendMessage(event.getClickType().name());
            event.getWhoClicked().sendMessage(event.getNPC().getId());
    }*/

//    @EventHandler
//    public void onPlayerQuit(PlayerQuitEvent event) {
//        System.out.println("saving player data");
//
//        GenericPlayer gp = Settlements.getInstance().getGpm().getOnlinePlayer(event.getPlayer().getUniqueId());
//
//        Settlements.getInstance().getGpm().savePlayerAsync(gp);
//        Settlements.getInstance().getGpm().unloadPlayer(gp.getId());
//
//    }
//
//    @EventHandler
//    public void onPreLoginProcess(AsyncPlayerPreLoginEvent event) {
//        Settlements.getInstance().getGpm().loadOfflinePlayerAsync(event.getUniqueId(), result -> event.allow());
//
//
//        Settlements.getInstance().getGpm().loadPlayerAsync(event.getUniqueId(), result -> System.out.println("GPM - Loaded - " + result.getName() + "/" + result.getId()));
//        Settlements.getInstance().getSpm().loadPlayerAsync(event.getUniqueId(), result -> System.out.println("SPM - Loaded - " + result.getName() + "/" + result.getId()));
//    }

//    @EventHandler
//    public void onPlayerProcess(PlayerLoginEvent event) {
//
//
//
//        GenericPlayer gp = Settlements.getInstance().getGpm().getOnlinePlayer(event.getPlayer().getUniqueId());
//
//    }

/*    @EventHandler
    public void onWorldLoad(PlayerJoinEvent event) {
        Location loc = new Location(event.getPlayer().getWorld(), -254.5, 78.0,246.5);


        NPC npc = Settlements.getInstance().getLibrary().createNPC(Arrays.asList("Test"));
        npc.setLocation(loc);
        npc.create();
        Bukkit.getOnlinePlayers().forEach(p -> npc.show(p));

    }*/
}
