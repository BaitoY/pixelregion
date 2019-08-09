package org.baito.sponge.pixelregion.playerdata;

import org.baito.sponge.pixelregion.regions.Region;

public class PlayerLink {
    public String UUID;
    public Region region;
    public boolean inRegion;
    public boolean sendNotif;

    PlayerLink(String UUID, Region r) {
        this.UUID = UUID;
        region = r;
        inRegion = true;
        sendNotif = true;
    }

    PlayerLink(String UUID) {
        this.UUID = UUID;
        inRegion = false;
        sendNotif = true;
    }

    public void toggleNotif() {
        sendNotif = !sendNotif;
    }
}
