package com.infrium.lobby.cosmetic;

import com.infrium.lobby.InfriumLobby;
import com.infrium.lobby.cosmetic.actives.Halo;
import com.infrium.lobby.cosmetic.actives.MidasTouch;
import com.infrium.lobby.cosmetic.actives.ParticleWings;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosmeticsManager {
    private Map<Player, Cosmetic> activeCosmetics = new HashMap<>();
    private Map<Player, Map<String, Cosmetic>> playerCosmetics = new HashMap<>();

    // Add this new field to store all available cosmetics
    private Map<String, Cosmetic> allCosmetics = new HashMap<>();
    public CosmeticsManager() {
        initializeCosmetics();
    }

    public void setCosmetic(Player player, Cosmetic cosmetic) {
        if (hasUnlockedCosmetic(player, cosmetic)) {
            removeCosmetic(player);
            cosmetic.apply(player);
            activeCosmetics.put(player, cosmetic);
        } else {
            player.sendMessage("This cosmetic is locked. Unlock it first!");
        }
    }

    public void removeCosmetic(Player player) {
        Cosmetic currentCosmetic = activeCosmetics.get(player);
        if (currentCosmetic != null) {
            currentCosmetic.remove(player);
            activeCosmetics.remove(player);
        }
    }

    public void unlockCosmetic(Player player, Cosmetic cosmetic) {
        Map<String, Cosmetic> cosmetics = playerCosmetics.computeIfAbsent(player, k -> new HashMap<>());
        cosmetics.put(cosmetic.getName(), cosmetic);
        cosmetic.unlock();
    }

    public boolean hasUnlockedCosmetic(Player player, Cosmetic cosmetic) {
        Map<String, Cosmetic> cosmetics = playerCosmetics.get(player);
        return cosmetics != null && cosmetics.containsKey(cosmetic.getName());
    }



    public void initializeCosmetics() {
        // Initialize all cosmetics here
        allCosmetics.put(Cosmetics.MIDAS_TOUCH.getName(), new MidasTouch(this));
        allCosmetics.put(Cosmetics.PARTICLE_WINGS.getName(), new ParticleWings(this));
        allCosmetics.put(Cosmetics.HALO.getName(), new Halo(this));
        // Add more cosmetics as needed


        for (Cosmetic cosmetic : allCosmetics.values()) {
            InfriumLobby.getInstance().getServer().getPluginManager().registerEvents((Listener) cosmetic, InfriumLobby.getInstance());
            InfriumLobby.getInstance().getLogger().info("register: " + cosmetic.name);
        }
    }

    public List<Cosmetic> getAllCosmetics() {
        return List.copyOf(allCosmetics.values());
    }

    public Cosmetic getCosmeticByName(String name) {
        return allCosmetics.get(name);
    }

    public Cosmetic getActiveCosmetic(Player player) {
        return activeCosmetics.get(player);
    }
}
