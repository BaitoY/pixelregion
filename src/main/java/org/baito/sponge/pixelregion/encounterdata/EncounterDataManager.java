package org.baito.sponge.pixelregion.encounterdata;

import org.baito.sponge.pixelregion.Config;

import java.io.File;

public class EncounterDataManager {
    public static EncounterData[] data;

    public static void generateEncounters(File[] f) {
        data = new EncounterData[f.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = new EncounterData(Config.readConfig(f[i]));
        }
    }

    public static EncounterData getData(String name) {
        for (EncounterData i : data) {
            if (i.name.equals(name)) return i;
        }
        return null;
    }
}
