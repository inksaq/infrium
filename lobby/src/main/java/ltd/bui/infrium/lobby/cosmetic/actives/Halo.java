package ltd.bui.infrium.lobby.cosmetic.actives;

import lombok.Getter;
import ltd.bui.infrium.lobby.InfriumLobby;
import ltd.bui.infrium.lobby.cosmetic.Cosmetic;
import ltd.bui.infrium.lobby.cosmetic.CosmeticsManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Halo extends Cosmetic implements Listener {

    @Getter
    private final CosmeticsManager cosmeticsManager;
    private final Set<UUID> activeUsers = new HashSet<>();
    private int taskId = -1;

    public Halo(CosmeticsManager cosmeticsManager) {
        super("Halo", true);
        this.cosmeticsManager = cosmeticsManager;
    }

    @Override
    public void apply(Player player) {
        activeUsers.add(player.getUniqueId());
        this.isActive = true;
        if (taskId == -1) {
            startHaloTask();
        }
    }

    @Override
    public void remove(Player player) {
        activeUsers.remove(player.getUniqueId());
        this.isActive = false;
        if (activeUsers.isEmpty() && taskId != -1) {
            InfriumLobby.getInstance().getServer().getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }

    @Override
    public void toggle(Player player) {
        super.toggle(player);
    }

    private void startHaloTask() {
        taskId = InfriumLobby.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(InfriumLobby.getInstance(), () -> {
            for (UUID uuid : activeUsers) {
                Player player = InfriumLobby.getInstance().getServer().getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    spawnHaloParticles(player);
                }
            }
        }, 0L, 2L).getTaskId(); // Run every 2 ticks (10 times per second)
    }

    private void spawnHaloParticles(Player player) {
        Location location = player.getLocation();
        float yaw = location.getYaw();

        double haloRadius = 0.5; // Radius of the halo
        double haloHeight = 2.2; // Height above the player's head
        int particleCount = 20; // Number of particles in the halo
        double angleIncrement = 2 * Math.PI / particleCount; // Angle between each particle

        for (int i = 0; i < particleCount; i++) {
            double angle = i * angleIncrement;
            double x = Math.cos(angle) * haloRadius;
            double z = Math.sin(angle) * haloRadius;
            double y = haloHeight;

            Location particleLoc = location.clone().add(x, y, z);
            player.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
        }
    }


}
