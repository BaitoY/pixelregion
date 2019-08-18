package org.baito.sponge.pixelregion;

import org.baito.sponge.pixelregion.encounterdata.EncounterDataManager;
import org.baito.sponge.pixelregion.eventflags.EventFlagManager;
import org.baito.sponge.pixelregion.playerdata.PlayerLinkManager;
import org.baito.sponge.pixelregion.regions.RegionManager;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    public static String fs = System.getProperty("file.separator");
    public static Path execDir = Sponge.getPluginManager().getPlugin("pixelregion").get().getSource().get();
    public static Path serverDir = execDir.getParent().getParent();
    public static Path configDir = serverDir.resolve("config");
    public static Path pxrDir = configDir.resolve("pixelregion");

    public static Asset exampleRegion;
    public static Asset exampleEncounter;
    public static Asset exampleExtMoveEncounter;
    public static Asset exampleForage;
    public static Asset exampleEvent;

    public static File[] regionConfigs;
    public static File[] encConfigs;
    public static File[] extMoveEncConfigs;
    public static File[] forageConfigs;
    public static File[] eventConfigs;

    Config() {
    }

    public static void setup() {
        exampleRegion = Sponge.getAssetManager().getAsset(Sponge.getPluginManager().getPlugin("pixelregion").get(), "exampleregion.json").get();
        exampleEncounter = Sponge.getAssetManager().getAsset(Sponge.getPluginManager().getPlugin("pixelregion").get(), "exampleenc.json").get();
        exampleExtMoveEncounter = Sponge.getAssetManager().getAsset(Sponge.getPluginManager().getPlugin("pixelregion").get(), "exampleExternalMoveEnc.json").get();
        exampleForage = Sponge.getAssetManager().getAsset(Sponge.getPluginManager().getPlugin("pixelregion").get(), "exampleforagedata.json").get();
        exampleEvent = Sponge.getAssetManager().getAsset(Sponge.getPluginManager().getPlugin("pixelregion").get(), "exampleevent.json").get();
        if (!new File(pxrDir.toString()).exists()) {
            new File(pxrDir.toString()).mkdirs();
        }
        if (!new File(pxrDir.resolve("regions").toString()).exists()) {
            new File(pxrDir.resolve("regions").toString()).mkdirs();
            try {
                exampleRegion.copyToDirectory(Paths.get(pxrDir.resolve("regions") + fs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!new File(pxrDir.resolve("encounterdata").toString()).exists()) {
            new File(pxrDir.resolve("encounterdata").toString()).mkdirs();
            try {
                exampleEncounter.copyToDirectory(Paths.get(pxrDir.resolve("encounterdata") + fs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!new File(pxrDir.resolve("externalencounterdata").toString()).exists()) {
            new File(pxrDir.resolve("externalencounterdata").toString()).mkdirs();
            try {
                exampleExtMoveEncounter.copyToDirectory(Paths.get(pxrDir.resolve("externalencounterdata") + fs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!new File(pxrDir.resolve("foragedata").toString()).exists()) {
            new File(pxrDir.resolve("foragedata").toString()).mkdirs();
            try {
                exampleForage.copyToDirectory(Paths.get(pxrDir.resolve("foragedata") + fs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!new File(pxrDir.resolve("events").toString()).exists()) {
            new File(pxrDir.resolve("events").toString()).mkdirs();
            try {
                exampleEvent.copyToDirectory(Paths.get(pxrDir.resolve("events") + fs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static JSONObject readConfig(File conf) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(conf));
            String line = null;
            String ls = System.getProperty("line.separator");
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(ls);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONObject(sb.toString());
    }

    public static void load() {
        encConfigs = new File(pxrDir.resolve("encounterdata") + fs).listFiles();
        if (encConfigs.length > 0) {
            EncounterDataManager.generateEncounters(encConfigs);
        }
        extMoveEncConfigs = new File(pxrDir.resolve("externalencounterdata") + fs).listFiles();
        if (extMoveEncConfigs.length > 0) {
            EncounterDataManager.generateExtEncounters(extMoveEncConfigs);
        }
        forageConfigs = new File(pxrDir.resolve("foragedata") + fs).listFiles();
        if (forageConfigs.length > 0) {
            EncounterDataManager.generateForagedata(forageConfigs);
        }
        eventConfigs = new File(pxrDir.resolve("events") + fs).listFiles();
        if (eventConfigs.length > 0) {
            EventFlagManager.generateEvents(eventConfigs);
        }
        regionConfigs = new File(pxrDir.resolve("regions") + fs).listFiles();
        if (regionConfigs.length > 0) {
            RegionManager.generateRegions(regionConfigs);
            PlayerLinkManager.setup();
        }
    }
}