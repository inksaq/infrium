package ltd.bui.infrium.game.components.weapon.energy.components.core.components;

import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;

public class EnergyCore extends CoreComponent {

    @Getter @Setter private double lifespan; /*Base lifespan is 2 years(in seconds),
     each added component to the Body reduces the lifespan on each component with lifespan(processor and chargecell(with different lifespans)
      but same calculation effecting all)
      lifespan is effected by Tier, Grade and Rarity*/
    @Getter @Setter private double rechargeDelay; // charging delay between max sustain and being able to output energy, cooldown(extra delay) may effect recharge rate and also upgrades, quickcharge, constantcharge may affect it too.
    @Getter @Setter private double rechargeRate; // Calculates rate at which energycore charges, affected by stability rating and heatRate and cooldown(cooldown in cooldown may not fire and half recharge rate with addition of extra recharge delay)
    @Getter @Setter private double stabilityRating; // percentage value 0 - 100.00% indicating processor stability with upgradecomponents
    @Getter @Setter private double stabilityDropRate; // calculated by taking into account, voltage rating, energyoutput rate, maxsustaintime, heatrate to generate a value when sustainTime increases to also decrease stability reducing it from stabilityRating overtime, cooldowns affect this too
    @Getter @Setter private double voltageRating; //Default is 1.05 affected by overclocking (calculated by taking into account(tier, rarity, grade) and multiplying by voltage rating(voltageRating can be affected by adding overvolt or supervolt and going under with undervolt)
    @Getter @Setter private double voltageThreshold; // default is (Tier*(Rarity/2) * Grade * voltage rating) * voltageRating
    @Getter @Setter private double outputEnergyRate; // output rate is calculated by having a base output rate calculated from(tier,grade,rarity) and them modifiers are added by the likes of overvolt,
    @Getter @Setter private double coreEnergyCapacitance; // capacitance is a set value(altered by overload component) and is where energy is taken when fired, no matter max sustain time, if output energyrate depletes capacitance before sustain time is reached, it will weaken output(only output recharge delay(until max sustain is hit))
    @Getter @Setter private double outputEnergyRateThreshold; // threshold is calculated by overload(requires overvolt of chargecell) and underload(no min requirement) core component upgrades
    @Getter @Setter private double sustainTime; // sustain time is counted when user holds down click, it draws energy from coreEnergyCapactiance and maintained by outputEnergyRate
    @Getter @Setter private double maxSustainRate; // calculated by added core component upgrades added(overload(requires chargecell to have overvolt),superload(requires overcharge,superclock and supervolt to achieve superload((, No requirement for underload))
    @Getter @Setter private double idleDrawRate; // calculates corecomponents and upgrades added, and takes charge from chargecell capacitance
    @Getter @Setter private double HeatRate; // calculates idle heat, all corecomponents and upgrades create a baseline, then when energyoutput happens, calculated by getting voltage rating and distance to voltage threshold, sustain and distance to max sustain(The need to cooldown)
    @Getter @Setter private double heatStabilityThreshold; // Heat Threshold before stability drop rate increases affecting stabilitiy rating, modified by framebody heat exchanging attachments

    public EnergyCore(Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.ENERGY_CORE);
        this.lifespan = grade.getLifespan();
    }
}
