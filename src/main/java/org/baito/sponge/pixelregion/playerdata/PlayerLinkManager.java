package org.baito.sponge.pixelregion.playerdata;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

public class PlayerLinkManager {
    public static PlayerLink[] links;

    public static void setup() {
        links = new PlayerLink[0];
    }

    public static void pushLink(PlayerLink l) {
        PlayerLink[] newLinks = new PlayerLink[links.length + 1];
        System.arraycopy(links, 0, newLinks, 0, links.length);
        newLinks[links.length] = l;
        links = newLinks.clone();
    }

    public static Player getPlr(String UUID) {
        return Sponge.getServer().getPlayer(UUID).get();
    }

    public static PlayerLink getLink(Player plr) {
        if (links.length > 0) {
            for (PlayerLink i : links) {
                if (i.UUID.equals(plr.getUniqueId().toString())) {
                    return i;
                }
            }
        }
        PlayerLink newLink = new PlayerLink(plr.getUniqueId().toString());
        pushLink(newLink);
        return links[links.length - 1];
    }
}
