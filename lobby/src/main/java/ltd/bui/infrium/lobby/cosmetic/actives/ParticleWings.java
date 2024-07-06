package ltd.bui.infrium.lobby.cosmetic.actives;

import lombok.Getter;
import ltd.bui.infrium.lobby.InfriumLobby;
import ltd.bui.infrium.lobby.cosmetic.Cosmetic;
import ltd.bui.infrium.lobby.cosmetic.CosmeticsManager;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ParticleWings extends Cosmetic implements Listener {

    @Getter
    private final CosmeticsManager cosmeticsManager;
    private final Set<UUID> activeUsers = new HashSet<>();
    private int taskId = -1;

    public ParticleWings(CosmeticsManager cosmeticsManager) {
        super("Particle Wings", true);
        this.cosmeticsManager = cosmeticsManager;
    }

    @Override
    public void apply(Player player) {
        activeUsers.add(player.getUniqueId());
        this.isActive = true;
        if (taskId == -1) {
            startWingsTask();
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

    private void startWingsTask() {
        taskId = InfriumLobby.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(InfriumLobby.getInstance(), () -> {
            for (UUID uuid : activeUsers) {
                Player player = InfriumLobby.getInstance().getServer().getPlayer(uuid);
                if (player != null && player.isOnline()) {
                    spawnWingParticles(player);
                }
            }
        }, 0L, 2L).getTaskId(); // Run every 2 ticks (10 times per second)
    }

    private void spawnWingParticles(Player player) {
        Location location = player.getLocation();
        float yaw = location.getYaw();

        double wingSpan = 1.4; // Adjust this value to change the wing width
        double wingHeight = 1.2; // Adjust this value to change the wing height
        double particleDensity = 0.3; // Adjust this value to change the number of particles
        double distanceBehind = 0.3; // Distance behind the player

        for (double angle = 0; angle <= Math.PI / 2; angle += particleDensity) {
            for (int side : new int[]{-1, 1}) { // Left and right wings
                double w = side * (Math.cos(angle) * wingSpan);
                double h = Math.sin(angle) * wingHeight;

                // Calculate the position relative to the player
                double x = (-Math.sin(yaw) * distanceBehind) - Math.sin(Math.toRadians(yaw + 90)) * w;
                double y = h;
                double z = (-Math.cos(yaw) * distanceBehind) + Math.cos(Math.toRadians(yaw + 90)) * w;

                Location particleLoc = location.clone().add(x, y, z);
                player.getWorld().spawnParticle(Particle.END_ROD, particleLoc, 1, 0, 0, 0, 0);
            }
        }
    }

}
