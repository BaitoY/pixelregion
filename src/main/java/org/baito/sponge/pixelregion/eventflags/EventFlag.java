package org.baito.sponge.pixelregion.eventflags;

import org.json.JSONObject;

public class EventFlag {
    public String name;

    EventFlag(JSONObject j) {
        try {
            if (!j.has("name")) {
                throw new NullPointerException("An Event has no \"name\"! Skipping...");
            }
            name = j.getString("name");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}

