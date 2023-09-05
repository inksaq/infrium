package ltd.bui.infrium.game.components.weapon.energy;

import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.ICoreComponent;

public class WeaponFrame {

    private CoreComponent chargeCell;
    private CoreComponent coreProcessor;
    private CoreComponent energyCore;
    private CoreComponent lenseConduit;

    public WeaponFrame() {
        FrameBody ewf = new FrameBody();

    }

    public void equipChargeCell(ICoreComponent chargeCell) {

    }
}
