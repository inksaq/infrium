package ltd.bui.infrium.game.components.weapon.gun;

import com.destroystokyo.paper.event.player.PlayerAttackEntityCooldownResetEvent;
import io.papermc.paper.event.player.PlayerStopUsingItemEvent;
import ltd.bui.infrium.game.Settlements;
import ltd.bui.infrium.game.components.testing.ui.WorkbenchGUI;
import ltd.bui.infrium.game.components.weapon.WeaponComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;

public class WeaponListener implements Listener {


    private WorkbenchGUI gui;


    public WeaponListener(Settlements plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.gui = new WorkbenchGUI(LegacyComponentSerializer.legacyAmpersand().deserialize("Shard Selector"), plugin);
    }


    @EventHandler
    public void onPlayerScope(PlayerStopUsingItemEvent event) {
        event.getPlayer().sendMessage("held " + event.getItem().getType().name()+ " for " + event.getTicksHeldFor());

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (getFrameBody(heldItem) != null){
                FrameBody fb = getFrameBody(heldItem);
                fb.updateLoreNBT(heldItem, fb);
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
            }

            if (GunRegistry.isGun(heldItem)) {
                player.sendMessage("shoot");
                InfantryWeapon gun = (InfantryWeapon) GunRegistry.getGunByName(heldItem.getItemMeta().getDisplayName());
                gun.shoot(player);
            }
        }
    }

    public FrameBody getFrameBody(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        return pdc.get(WeaponComponent.getInstance().getWeaponKey(), WeaponComponent.getInstance().getFrameBodyDataType());
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
        event.setCancelled(true);
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (GunRegistry.isGun(heldItem)) {
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
