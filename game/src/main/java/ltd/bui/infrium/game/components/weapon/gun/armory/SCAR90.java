package ltd.bui.infrium.game.components.weapon.gun.armory;

import lombok.Getter;
import ltd.bui.infrium.core.item.ItemBuilder;
import ltd.bui.infrium.game.components.weapon.gun.InfantryWeapon;
import ltd.bui.infrium.game.components.weapon.gun.armory.projectiles.Projectile;
import ltd.bui.infrium.game.components.weapon.gun.armory.projectiles.VillagerParticleProjectile;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SCAR90 extends InfantryWeapon {
    private final Projectile projectile;

    @Getter
    private String name;
    private int magazineSize;
    private int bulletsLeft;
    private double bulletSpeed;  // The actual speed of the bullet
    private double bulletStop;   // Range after which bullet stops
    private boolean isReloading;


    public SCAR90(String name) {
        super(ItemBuilder.builder().setMaterial(Material.DIAMOND_HOE).setName(Component.text(name)).build(true), 5.0,30, 30); // Example item and damage
        this.name = name;
        this.projectile = new VillagerParticleProjectile(200, 5.0, 0.005, false); // example speed, damage, bullet drop, penetration
        this.magazineSize = 30;
        this.bulletsLeft = magazineSize;
        this.bulletSpeed = 1.5;
        this.bulletStop = 100.0;
        this.isReloading = false;
    }

    public void registerGun(){

    }

    public void shoot(Player player) {
        if (bulletsLeft <= 0 || isReloading) {
            return;
        }
        Vector trajectory = player.getEyeLocation().getDirection();
        projectile.launch(player.getEyeLocation(), trajectory);
        player.sendMessage("scar fired");
        bulletsLeft--;
    }

    public void reload() {
        // Reload logic
        // For demonstration, instant reload
        bulletsLeft = magazineSize;
    }

    public ItemStack getItem() {
        return this.getGunItem();
    }

    // Getters, Setters, and other methods
}