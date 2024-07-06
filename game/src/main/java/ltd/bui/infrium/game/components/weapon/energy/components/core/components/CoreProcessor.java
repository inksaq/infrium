package ltd.bui.infrium.game.components.weapon.energy.components.core.components;

import de.tr7zw.changeme.nbtapi.NBTCompound;
import lombok.Getter;
import lombok.Setter;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponent;
import ltd.bui.infrium.game.components.weapon.energy.components.core.CoreComponentType;
import ltd.bui.infrium.game.components.weapon.energy.components.core.components.upgrades.ComponentUpgrade;
import ltd.bui.infrium.game.item.Grade;
import ltd.bui.infrium.game.item.Rarity;
import ltd.bui.infrium.game.item.Tier;
import org.bukkit.ChatColor;

import java.util.*;

@Setter
@Getter
public class CoreProcessor extends CoreComponent {

    private FrameBody frameBodyParent;
    private UUID uuid;
    private CoreProcessor coreProcessor;
    private double lifespan; // lifespan of chargecell(total chargeRate throughput x total outputRate + capacity) //TODO

    private int gigaHertz; // Processor bandwidth for componenets (component tier ghz + overclock multiplier) //TODO
//    @Getter @Setter private int gigaHertzLimit; // Processor bandwidth for componenets (component tier ghz + overclock multiplier) //TODO
    private int gigaHertzReq; // Price for a component upgrade limitation
    private int idleDrawRate; // amount of idle draw of from energycore to power processor
    private int heatRate; // heat output per second of idle time / increases when output rate decreases(outputRate X tier X grade
//    @Getter @Setter private int OverClockThreshold;
    private Integer upgradeLimit;
    private Set<ComponentUpgrade<?>> componentUpgrades; // OverVolt, OverCharge, UnderVolt, UnderCharge (all affect lifespan,chargeRate,outputRate and heatRate)


    public CoreProcessor(FrameBody parentFrameBody,Rarity rarity, Grade grade, Tier tier) {
        super(rarity, grade, tier, CoreComponentType.CORE_PROCESSOR);
        frameBodyParent = parentFrameBody;
        lifespan = grade.getLifespan();

        gigaHertz = tier.getGigaHertz();
//        gigaHertzLimit = rarity.getComponentUpgradeLimit();
        gigaHertzReq = tier.getGigahertzUpgradeRequirement();
        idleDrawRate = tier.getIdleDraw();
        heatRate = tier.getHeatRate();
//        OverClockThreshold = tier.getGigaHertz() * rarity.getThresholdMultiplier();
        upgradeLimit = rarity.getComponentUpgradeLimit();
        componentUpgrades = new HashSet<>();
    }

    public List<String> getCoreProcessorLore() {
        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.GRAY + "---------------------");
        lore.add(ChatColor.GOLD + "" + ChatColor.BOLD + "Core Processor");
        lore.add(ChatColor.GRAY + "---------------------");
        lore.add(ChatColor.GRAY + "UUID: " + ChatColor.WHITE + coreProcessor.getUuid());
        lore.add(ChatColor.GRAY + "Tier: " + ChatColor.WHITE + coreProcessor.getTier());
        lore.add(ChatColor.GRAY + "Grade: " + ChatColor.WHITE + coreProcessor.getGrade());
        lore.add(ChatColor.GRAY + "Rarity: " + ChatColor.WHITE + coreProcessor.getRarity());
        lore.add(ChatColor.GRAY + "Lifespan: " + ChatColor.WHITE + coreProcessor.getLifespan() + "s");
        lore.add(ChatColor.GRAY + "GigaHertz: " + ChatColor.WHITE + coreProcessor.getGigaHertz() + " GHz");
        lore.add(ChatColor.GRAY + "GigaHertz Req: " + ChatColor.WHITE + coreProcessor.getGigaHertzReq() + " GHz");
        lore.add(ChatColor.GRAY + "Idle Draw: " + ChatColor.WHITE + coreProcessor.getIdleDrawRate() + "u/s");
        lore.add(ChatColor.GRAY + "Heat Rate: " + ChatColor.WHITE + coreProcessor.getHeatRate() + " °C/s");
        lore.add(ChatColor.GRAY + "Upgrades: " + (coreProcessor.getComponentUpgrades() != null ? "[" + coreProcessor.getComponentUpgrades().size() + "/" + coreProcessor.getUpgradeLimit() + "] (click for upgrades)" : "[0/0] (click for upgrades)"));
        if (coreProcessor.getComponentUpgrades() != null) {
            coreProcessor.getComponentUpgrades().forEach(upgrade -> lore.add(ChatColor.GRAY + "  - " + upgrade.getComponentUpgradeType().name()));
        }
        lore.add(ChatColor.GRAY + "---------------------");

        return lore;
    }
    @Override
    public NBTCompound serializeToNBT() {
        return null;
    }

    @Override
    public CoreComponent deserializeFromNBT(NBTCompound nbt) {
        return null;
    }
}
