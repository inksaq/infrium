package com.infrium.game.components.weapon;

import lombok.Getter;
import com.infrium.core.api.components.Component;
import com.infrium.game.Settlements;
import com.infrium.game.components.testing.commands.TestCommand;
import com.infrium.game.components.testing.listeners.TestListener;
import com.infrium.game.components.testing.ui.WeaponWorkstationGUI;
import com.infrium.game.components.weapon.energy.components.gui.EnergyCoreDataType;
import com.infrium.game.components.weapon.energy.components.gui.FrameBodyDataType;
import com.infrium.game.components.weapon.gun.GunRegistry;
import com.infrium.game.components.weapon.gun.WeaponListener;
import com.infrium.game.components.weapon.gun.armory.SCAR90;
import com.infrium.game.components.weapon.gun.commands.GetWeaponCC;
import com.infrium.game.components.weapon.registry.WeaponDevelopmentSystem;
import com.infrium.game.components.weapon.registry.WeaponRegistry;
import com.infrium.game.components.weapon.workstation.weapon.WeaponWorkstation;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WeaponComponent extends Component<Settlements> {
    private static WeaponComponent instance;
    private final SCAR90 scar90; // Your gun

    private JavaPlugin plugin = Settlements.getInstance();

    @Getter
    private final FrameBodyDataType frameBodyDataType;
    @Getter
    private final EnergyCoreDataType energyCoreDataType;

    @Getter
    private final NamespacedKey weaponKey;

    @Getter
    private WeaponRegistry weaponRegistry;

    @Getter
    private WeaponDevelopmentSystem weaponDevelopmentSystem;
    @Getter private WeaponWorkstation workstation;
    @Getter private WeaponWorkstationGUI workstationGUI;


    public WeaponComponent() {
        instance = this;
        weaponRegistry = new WeaponRegistry();
        this.weaponKey = new NamespacedKey(Settlements.getInstance(), "ergwp");
        this.frameBodyDataType = new FrameBodyDataType(plugin);
        this.energyCoreDataType = new EnergyCoreDataType(plugin);
        scar90 = new SCAR90("scar90");
//        WeaponRegistry.registerWeapons();
    }

    public void openWorkstationGUI(Player player) {
        workstation.openWorkstation(player);
    }

//    public FrameBody getItemFrameBody(ItemStack itemStack) {
//        var fbNBT = NBT.itemStackToNBT(itemStack);
//        fbNBT.
//        return
//    }



    public static WeaponComponent getInstance() {
        return instance;
    }

    @Override
    public void enable(Settlements plugin) {
        if (instance == null) instance = new WeaponComponent();

        GunRegistry.registerGun(scar90.getGunItem(), scar90, scar90.getName());
        this.workstation = new WeaponWorkstation(plugin);
        weaponDevelopmentSystem = new WeaponDevelopmentSystem(plugin, workstation);
        // Schedule the item ticking task
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            weaponDevelopmentSystem.tickItems();
        }, 0L, 1L); // Run every tick (20 times per second)


    }

    @Override
    public void disable(Settlements plugin) {

    }

    @Override
    public void registerListener(Settlements plugin) {
        new TestListener(plugin);
        new WeaponListener(plugin);
    }

    @Override
    public void registerCommands(Settlements plugin) {
        new TestCommand();
        new GetWeaponCC();
    }


}

