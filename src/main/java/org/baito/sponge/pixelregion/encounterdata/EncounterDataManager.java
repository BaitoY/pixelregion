package org.baito.sponge.pixelregion.encounterdata;

import org.baito.sponge.pixelregion.Config;
import org.baito.sponge.pixelregion.encounterdata.external.ExternalEncounterData;
import org.baito.sponge.pixelregion.encounterdata.external.ForageData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EncounterDataManager {
    public static Map<String, EncounterData> encounterData = new HashMap<>();
    public static Map<String, ExternalEncounterData> externalEncounterData = new HashMap<>();
    public static Map<String, ForageData> forageData = new HashMap<>();

    public static void generateEncounters(File[] f) {
        for (int i = 0; i < f.length; i++) {
            EncounterData ed = new EncounterData(Config.readConfig(f[i]));
            encounterData.put(ed.name, ed);
        }
    }

    public static void generateExtEncounters(File[] f) {
        for (int i = 0; i < f.length; i++) {
            ExternalEncounterData ed = new ExternalEncounterData(Config.readConfig(f[i]));
            externalEncounterData.put(ed.name, ed);
        }
    }

    public static void generateForagedata(File[] f) {
        for (int i = 0; i < f.length; i++) {
            ForageData ed = new ForageData(Config.readConfig(f[i]));
            forageData.put(ed.name, ed);
        }
    }

    public static EncounterData getData(String name) {
        if (encounterData.get(name) != null) {
            return encounterData.get(name);
        }
        return null;
    }

    public static ExternalEncounterData getExtMoveData(String name) {
        if (externalEncounterData.get(name) != null) {
            return externalEncounterData.get(name);
        }
        return null;
    }

    public static ForageData getForageData(String name) {
        if (forageData.get(name) != null) {
            return forageData.get(name);
        }
        return null;
    }
}
