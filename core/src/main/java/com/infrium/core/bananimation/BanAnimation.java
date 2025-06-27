package com.infrium.core.bananimation;

import com.infrium.api.punishments.Punishment;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Squid;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BanAnimation extends BukkitRunnable {

  private final Player player;
  private final Punishment punishment;
  private final List<Squid> entities = new ArrayList<>();
  private final double rPerTick = 0.48;
  private long ticks = 0;
  private int tick = 0;
  private double rad = 3;
  private double radSpot = 0.0;

  public BanAnimation(Player player, Punishment punishment) {
    this.player = player;
    this.punishment = punishment;
    this.entities.add(player.getWorld().spawn(player.getLocation(), Squid.class));
    this.entities.add(player.getWorld().spawn(player.getLocation(), Squid.class));
    this.entities.add(player.getWorld().spawn(player.getLocation(), Squid.class));
    this.entities.add(player.getWorld().spawn(player.getLocation(), Squid.class));
    entities.forEach(
        squid -> {
          squid.setInvulnerable(true);
          squid.setGlowing(true);
          squid.setSilent(true);
          squid.setCustomNameVisible(false);
          squid.setCollidable(false);
          squid.setGravity(false);
          squid.setAI(false);
        });
  }

  private static Location getLocationAroundCircle(
      Location center, double radius, double angleInRadian, double y) {
    double x = center.getX() + radius * Math.cos(angleInRadian);
    double z = center.getZ() + radius * Math.sin(angleInRadian);

    Location loc = new Location(center.getWorld(), x, y, z);
    Vector difference = center.toVector().clone().subtract(loc.toVector());
    loc.setDirection(difference);
    loc.setY(y);

    return loc;
  }

  private static void lookAtPlayer(Squid entity, Player player) {

    double var4 = entity.getLocation().getX() - player.getLocation().getX();
    double var8 = entity.getLocation().getZ() - player.getLocation().getZ();

    float pitch_to = (float) Math.sqrt(var4 * var4 + var8 * var8);
    float yaw_to = (float) (Math.atan2(var8, var4) * 180.0D / Math.PI) - 90.0F;

    entity.getEyeLocation().setYaw(yaw_to);
    entity.getEyeLocation().setPitch(pitch_to);
  }

  @Override
  public void run() {

    for (Squid sq : entities) {
      Location l =
          getLocationAroundCircle(
              this.player.getLocation(),
              rad,
              rPerTick * tick + radSpot,
              this.player.getLocation().getY() + 3);
      lookAtPlayer(sq, this.player);
      sq.teleport(l);
      radSpot += 1.5;
    }

    if (tick > 200) { // max 10 seconds
      this.player.kick(
          LegacyComponentSerializer.legacyAmpersand().deserialize(punishment.getReason()));
      this.cancel();
    }

    rad -= 0.005;
    tick++;
  }

  public void cancel() {
    entities.forEach(Entity::remove); // remove entities
    super.cancel();
  }
}
