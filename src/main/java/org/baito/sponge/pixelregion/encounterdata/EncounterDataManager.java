package org.baito.sponge.pixelregion.encounterdata;

import org.baito.sponge.pixelregion.Config;
import org.baito.sponge.pixelregion.encounterdata.external.ExternalMoveEncounterData;
import org.baito.sponge.pixelregion.encounterdata.external.ForageData;

import java.io.File;

public class EncounterDataManager {
    public static EncounterData[] encounterData;
    public static ExternalMoveEncounterData[] extEncounterData;
    public static ForageData[] forageData;

    public static void generateEncounters(File[] f) {
        encounterData = new EncounterData[f.length];
        for (int i = 0; i < encounterData.length; i++) {
            encounterData[i] = new EncounterData(Config.readConfig(f[i]));
        }
    }

    public static void generateExtEncounters(File[] f) {
        extEncounterData = new ExternalMoveEncounterData[f.length];
        for (int i = 0; i < extEncounterData.length; i++) {
            extEncounterData[i] = new ExternalMoveEncounterData(Config.readConfig(f[i]));
        }
    }

    public static void generateForagedata(File[] f) {
        forageData = new ForageData[f.length];
        for (int i = 0; i < extEncounterData.length; i++) {
            forageData[i] = new ForageData(Config.readConfig(f[i]));
        }
    }

    public static EncounterData getData(String name) {
        for (EncounterData i : encounterData) {
            if (i.name.equals(name)) return i;
        }
        return null;
    }

    public static ExternalMoveEncounterData getExtMoveData(String name) {
        for (ExternalMoveEncounterData i : extEncounterData) {
            if (i.name.equals(name)) return i;
        }
        return null;
    }

    public static ForageData getForageData(String name) {
        for (ForageData i : forageData) {
            if (i.name.equals(name)) return i;
        }
        return null;
    }
}
