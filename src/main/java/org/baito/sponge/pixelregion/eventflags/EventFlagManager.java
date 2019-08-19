package org.baito.sponge.pixelregion.eventflags;

import org.baito.sponge.pixelregion.Config;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EventFlagManager {
    public static Map<String, EventFlag> events = new HashMap<>();

    public static void generateEvents(File[] f) {
        for (int i = 0; i < f.length; i++) {
            EventFlag e = new EventFlag(Config.readConfig(f[i]));
            events.put(e.name, e);
        }
    }

    public static EventFlag getFlag(String name) {
        if (events.get(name) != null) {
            return events.get(name);
        }
        return null;
    }
}
