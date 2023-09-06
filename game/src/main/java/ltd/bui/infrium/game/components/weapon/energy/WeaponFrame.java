package ltd.bui.infrium.game.components.weapon.energy;

import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.ICoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.EnergyCore;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public class WeaponFrame {

    private CoreComponent chargeCell;
    private CoreComponent coreProcessor;
    private CoreComponent energyCore;
    private CoreComponent lenseConduit;

    public WeaponFrame() {
        EnergyCore ec = new EnergyCore(Rarity.MASS, Grade.FACTORY, Tier.II);
        FrameBody ewf = new FrameBody(Grade.CHIPPED);
        ewf.addEnergyCore(ec);


    }

    public void equipChargeCell(ICoreComponent chargeCell) {

    }
}
