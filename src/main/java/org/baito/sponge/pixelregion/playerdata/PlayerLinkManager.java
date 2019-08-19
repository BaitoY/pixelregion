package org.baito.sponge.pixelregion.playerdata;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerLinkManager {
    public static Map<String, PlayerLink> links = new HashMap<>();

    public static void addLink(PlayerLink l) {
        links.put(l.UUID, l);
    }

    public static Player getPlr(String UUID) {
        return Sponge.getServer().getPlayer(UUID).get();
    }

    public static PlayerLink getLink(Player plr) {
        if (!links.isEmpty()) {
            if (links.get(plr.getUniqueId() + "") != null) {
                return links.get(plr.getUniqueId() + "");
            }
        }
        PlayerLink newLink = new PlayerLink(plr.getUniqueId().toString());
        links.put(plr.getUniqueId() + "", newLink);
        return newLink;
    }
}
