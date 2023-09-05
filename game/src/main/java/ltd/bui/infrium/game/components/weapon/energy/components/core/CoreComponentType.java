package ltd.bui.infrium.game.components.weapon.energy.components.core;

import ltd.bui.infrium.game.components.weapon.energy.components.FrameBody;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.ChargeCell;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.CoreProcessor;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.EnergyCore;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.LenseConduit;

public enum CoreComponentType {
    CHARGE_CELL(ChargeCell.class),
    LENSE_CONDUIT(LenseConduit.class),
    CORE_PROCESSOR(CoreProcessor.class),
    ENERGY_CORE(EnergyCore.class);

    private final Class<? extends CoreComponent> associatedClass;

    CoreComponentType(Class<? extends CoreComponent> associatedClass) {
        this.associatedClass = associatedClass;
    }

    public Class<? extends CoreComponent> getAssociatedClass() {
        return associatedClass;
    }
}
