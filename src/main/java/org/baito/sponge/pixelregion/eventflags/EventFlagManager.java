package org.baito.sponge.pixelregion.eventflags;

import org.baito.sponge.pixelregion.Config;
import java.io.File;

public class EventFlagManager {
    public static EventFlag[] events;

    public static void generateEvents(File[] f) {
        events = new EventFlag[f.length];
        for (int i = 0; i < events.length; i++) {
                events[i] = new EventFlag(Config.readConfig(f[i]));
        }
    }

    public static EventFlag getFlag(String name) {
        for (EventFlag i : events) {
            if (i.name.equals(name)) return i;
        }
        return null;
    }
}
