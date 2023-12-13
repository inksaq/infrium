package ltd.bui.infrium.game.components.weapon.gun;

import com.destroystokyo.paper.event.player.PlayerAttackEntityCooldownResetEvent;
import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import lombok.Getter;
import ltd.bui.infrium.game.Settlements;
import ltd.bui.infrium.game.components.testing.ui.WorkbenchGUI;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.registry.WeaponRegistry;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class WeaponListener implements Listener {


    @Getter
    private WorkbenchGUI gui;


    public WeaponListener(Settlements plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.gui = new WorkbenchGUI(LegacyComponentSerializer.legacyAmpersand().deserialize("Weapon"), plugin);

    }


    @EventHandler
    public void onPlayerScope(PlayerStopUsingItemEvent event) {
        event.getPlayer().sendMessage("held " + event.getItem().getType().name()+ " for " + event.getTicksHeldFor());

    }

    @EventHandler
    public void onWeaponGUI(PlayerDropItemEvent event) {

            if (WeaponRegistry.getInstance().isFrameBody(event.getItemDrop().getItemStack())){
                event.setCancelled(true);
                gui.openInventory(event.getPlayer());
            }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            FrameBody fb = WeaponRegistry.getInstance().getFrameBody(heldItem);
            if (fb != null){

                fb.tickFrameBody();
                fb.updateLoreNBT(heldItem, fb);
                fb.updateRegistry(fb);
                player.sendMessage("UUID:" + fb.getFrameUUID());
                player.sendMessage("Lifespan: " + fb.getLifespan());
                player.sendMessage("EnergyCore:" + (fb.getEnergyCore() != null ? "installed" : "not installed"));
                if (fb.getEnergyCore() != null) {
                    var ec = fb.getEnergyCore();
                    player.sendMessage("UUID:" + ec.getUuid());
                    player.sendMessage("Lifespan: " + ec.getLifespan());
                    player.sendMessage("IdleDrawRate: " + ec.getIdleDrawRate());
                    player.sendMessage("EnergyCore Capacitance:" + ec.getCoreEnergyCapacitance());
                }
                player.sendMessage("ChargeCell:" + (fb.getChargeCell() != null ? "installed" : "not installed"));

                if (fb.getChargeCell() != null) {
                    var cc = fb.getChargeCell();
                    player.sendMessage("Lifespan:" + cc.getLifespan());
                    player.sendMessage("parent: " + cc.getFrameBodyParent().getFrameUUID());
                    player.sendMessage("chargerate: " + cc.getCurrentChargeRate());
                    player.sendMessage("outputrate:" + cc.getCurrentOutputRate());
                }
            }

            if (GunRegistry.isGun(heldItem)) {
                player.sendMessage("shoot");
                InfantryWeapon gun = (InfantryWeapon) GunRegistry.getGunByName(heldItem.getItemMeta().getDisplayName());
                gun.shoot(player);
            }
        }
    }


    public FrameBody getFrameBody(ItemStack itemstack, FrameBody weaponData) {
        return null;
    }

    public void onHornSound(PlayerStopUsingItemEvent event) {

    }

    public void onHornSound(PlayerAttackEntityCooldownResetEvent event) {

    }



    @EventHandler
    public void onPlayerSwapHands(PlayerSwapHandItemsEvent event) {

        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (GunRegistry.isGun(heldItem)) {
            event.setCancelled(true);
            InfantryWeapon gun = (InfantryWeapon) GunRegistry.getGunByName(heldItem.getItemMeta().getDisplayName());

            gun.reload();
            player.sendMessage("reload");
        }
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Vector damagePoint = damaged.getEyeLocation().toVector();  // Assuming the damage point is the eye location for simplicity

            double headHeight = damaged.getEyeLocation().getY() - 0.5;  // This is a very simplistic mechanism to determine a headshot
            if (damagePoint.getY() >= headHeight) {
                event.setDamage(event.getDamage() * 2);  // Double the damage for headshots
                // You can also send messages or apply other effects for headshots
            }
        }
    }
}
