package ltd.bui.infrium.game.components.weapon.energy.components;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.attachments.FrameAttachment;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.ChargeCell;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.CoreProcessor;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.EnergyCore;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.LenseConduit;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgradeType;

import java.util.Set;

public class FrameBody{

    @Getter @Setter
    private FrameBody frameBody;
    @Getter @Setter
    private Set<FrameAttachment> frameAttachments;
    @Getter @Setter
    private ChargeCell chargeCell;
    @Getter @Setter
    private EnergyCore energyCore;
    @Getter @Setter
    private CoreProcessor coreProcessor;
    @Getter @Setter
    private LenseConduit lenseConduit;



    public FrameBody(){
        frameBody = this;

    }

    public void addChargeCellToFrame(CoreComponent coreComponent) {

    }
    public boolean isApplyingCorrectCoreComponent(CoreComponent coreComponent) {


        return true;
    }


    public boolean addUpgradeToChargeCell(ComponentUpgrade componentUpgrade) {
        if (!(isApplyingCorrectUpgrade(chargeCell, componentUpgrade))) {
            return false;
        }
        if (!canAddSuperUpgrade(componentUpgrade)) {
            return false;
        }
        if (chargeCell.hasHitUpgradeLimit()){
            return false;
        }
        chargeCell.addUpgrade(componentUpgrade);
        return true;
    }



    public boolean isApplyingCorrectUpgrade(CoreComponent coreComponent, ComponentUpgrade componentUpgrade) {
        var componentUpgradeType = componentUpgrade.getComponentUpgradeType();
        if (!(componentUpgradeType.getCoreComponentType() == coreComponent.getComponentType())) {
            System.out.println("You may only apply " + componentUpgradeType.getCoreComponentType().toString().toUpperCase()
                    + " component upgrade on " + coreComponent.getComponentType().toString().toUpperCase() + "'s");
            return false;
        }
        return true;
    }



    public boolean canAddSuperUpgrade(ComponentUpgrade componentUpgrade) {
        if (isSuperUpgrade(componentUpgrade.getComponentUpgradeType()) && !chargeCellHasOverCharge()) {
            System.out.println("You need OverCharge upgrade in ChargeCell to add " + componentUpgrade.getComponentUpgradeType());
            return false;
        }
        // You can add more conditions here if you need to check other dependencies for other upgrades

        return true;
    }

    public boolean addUpgradeToEnergyCore(ComponentUpgrade componentUpgrade) {
        if (!isApplyingCorrectUpgrade(energyCore, componentUpgrade)){
            return false;
        }
        if (!canAddSuperUpgrade(componentUpgrade)) {
            return false;
        }
        if (chargeCell.hasHitUpgradeLimit()){
            return false;
        }
        chargeCell.addUpgrade(componentUpgrade);


        energyCore.addUpgrade(componentUpgrade);
        return true;
    }

    public boolean addUpgradeToCoreProcessor(ComponentUpgrade componentUpgrade) {
        if (!canAddUpgradeToCore(componentUpgrade)) {
            return false;
        }

        coreProcessor.addUpgrade(componentUpgrade);
        return true;
    }

    private boolean isSuperUpgrade(ComponentUpgradeType upgradeType) {
        return upgradeType == ComponentUpgradeType.SUPERCLOCK ||
                upgradeType == ComponentUpgradeType.SUPERVOLT ||
                upgradeType == ComponentUpgradeType.SUPERLOAD;
    }

    private boolean chargeCellHasOverCharge() {
        return chargeCell.getUpgrades().stream().anyMatch(upgrade -> upgrade.getComponentUpgradeType() == ComponentUpgradeType.OVERCHARGE);
    }

}
