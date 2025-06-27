package com.infrium.smpqol.beacon;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

public class NavigatorCompass {
    private final UUID playerId;
    @Getter
    private BeaconWarp trackedBeacon;
    private boolean isTracking;

    public NavigatorCompass(Player player) {
        this.playerId = player.getUniqueId();
        this.isTracking = false;
    }

    public void setTrackedBeacon(BeaconWarp beacon) {
        this.trackedBeacon = beacon;
        this.isTracking = true;
    }

    public boolean isTracking() {
        return isTracking;
    }
}
