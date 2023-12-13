package ltd.bui.infrium.game.components.weapon.energy.components.core;

import ltd.bui.infrium.game.components.weapon.energy.components.core.components.*;

public enum CoreComponentType {
    EMPTY(null),
    FRAME_BODY(FrameBody.class),
    CHARGE_CELL(ChargeCell.class),
    LENS_CONDUIT(LensConduit.class),
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
