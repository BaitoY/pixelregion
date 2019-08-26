package org.baito.sponge.pixelregion.regions;

import org.baito.sponge.pixelregion.Config;
import org.spongepowered.api.entity.living.player.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RegionManager {
    public static Map<String, Region> allRegions = new HashMap<>();

    public static void generateRegions(File[] f) {
        for (int i = 0; i < f.length; i++) {
            Region r = new Region(Config.readConfig(f[i]));
            allRegions.put(r.name, r);
        }
    }

    public static Region getRegionInside(Player plr) {
        int x = plr.getPosition().getFloorX();
        int y = plr.getPosition().getFloorY();
        int z = plr.getPosition().getFloorZ();
        Region location = null;
        if (allRegions.values().size() > 0) {
            for (Region i : allRegions.values()) {
                if (!i.world.equals(plr.getWorld().getProperties().getWorldName())) {
                    continue;
                }
                if (i.yDim == null) {
                    if (i.polygon.contains(x, z)) {
                        if (location != null && i.weight > location.weight) {
                            // If location is not null, and the current tested region weights higher
                            location = i;
                        } else if (location == null) {
                            // If location is null
                            location = i;
                        }
                    }
                } else if (i.polygon.contains(x, z) && y >= i.yDim[0] && y <= i.yDim[1]) {
                    if (location != null && i.weight > location.weight) {
                        // If location is not null, and the current tested region weights higher
                        location = i;
                    } else if (location == null) {
                        // If location is null
                        location = i;
                    }
                }
            }
        }
        return location;
    }
}
