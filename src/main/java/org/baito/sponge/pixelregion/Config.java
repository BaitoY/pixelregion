package org.baito.sponge.pixelregion;

import org.baito.sponge.pixelregion.encounterdata.EncounterDataManager;
import org.baito.sponge.pixelregion.eventflags.EventFlagManager;
import org.baito.sponge.pixelregion.eventflags.PlayerFlagDataManager;
import org.baito.sponge.pixelregion.regions.RegionManager;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Config {
    public static String fs = System.getProperty("file.separator");
    public static Path execDir = Sponge.getPluginManager().getPlugin("pixelregion").get().getSource().get();
    public static Path pxrDir = execDir.getParent().getParent().resolve("config").resolve("pixelregion");

    Config() {
    }

    public static void setup() {
        if (!new File(pxrDir.toString()).exists()) {
            new File(pxrDir.toString()).mkdirs();
        }
        if (!new File(pxrDir.resolve("regions").toString()).exists()) {
            new File(pxrDir.resolve("regions").toString()).mkdirs();
            try {
                Sponge.getAssetManager().getAsset(Sponge.getPluginManager().getPlugin("pixelregion").get(), "exampleregion.json")
                        .get().copyToDirectory(Paths.get(pxrDir.resolve("regions") + fs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!new File(pxrDir.resolve("encounterdata").toString()).exists()) {
            new File(pxrDir.resolve("encounterdata").toString()).mkdirs();
            try {
                Sponge.getAssetManager().getAsset(Sponge.getPluginManager().getPlugin("pixelregion").get(), "exampleenc.json").get().
                        copyToDirectory(Paths.get(pxrDir.resolve("encounterdata") + fs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!new File(pxrDir.resolve("externalencounterdata").toString()).exists()) {
            new File(pxrDir.resolve("externalencounterdata").toString()).mkdirs();
            try {
                Sponge.getAssetManager().getAsset(Sponge.getPluginManager().getPlugin("pixelregion").get(), "exampleExternalMoveEnc.json").get()
                        .copyToDirectory(Paths.get(pxrDir.resolve("externalencounterdata") + fs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!new File(pxrDir.resolve("foragedata").toString()).exists()) {
            new File(pxrDir.resolve("foragedata").toString()).mkdirs();
            try {
                Sponge.getAssetManager().getAsset(Sponge.getPluginManager().getPlugin("pixelregion").get(), "exampleforagedata.json").get()
                        .copyToDirectory(Paths.get(pxrDir.resolve("foragedata") + fs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!new File(pxrDir.resolve("events").toString()).exists()) {
            new File(pxrDir.resolve("events").toString()).mkdirs();
            try {
                Sponge.getAssetManager().getAsset(Sponge.getPluginManager().getPlugin("pixelregion").get(), "exampleevent.json").get()
                        .copyToDirectory(Paths.get(pxrDir.resolve("events") + fs));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!new File(pxrDir.resolve("events").resolve("playerdata").toString()).exists()) {
            new File(pxrDir.resolve("events").resolve("playerdata").toString()).mkdirs();
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
        File[] encConfigs = getAllFilesInDirectory(new File(pxrDir.resolve("encounterdata") + fs));
        if (encConfigs.length > 0) {
            EncounterDataManager.generateEncounters(encConfigs);
        }
        File[] extMoveEncConfigs = getAllFilesInDirectory(new File(pxrDir.resolve("externalencounterdata") + fs));
        if (extMoveEncConfigs.length > 0) {
            EncounterDataManager.generateExtEncounters(extMoveEncConfigs);
        }
        File[] forageConfigs = getAllFilesInDirectory(new File(pxrDir.resolve("foragedata") + fs));
        if (forageConfigs.length > 0) {
            EncounterDataManager.generateForageData(forageConfigs);
        }
        File[] eventConfigs = new File(pxrDir.resolve("events") + fs).listFiles(File::isFile);
        if (eventConfigs != null && eventConfigs.length > 0) {
            EventFlagManager.generateEvents(eventConfigs);
        }
        File[] playerData = getAllFilesInDirectory(new File(pxrDir.resolve("events").resolve("playerdata") + fs));
        if (playerData.length > 0) {
            PlayerFlagDataManager.generateData(playerData);
        }
        File[] regionConfigs = getAllFilesInDirectory(new File(pxrDir.resolve("regions") + fs));
        if (regionConfigs.length > 0) {
            RegionManager.generateRegions(regionConfigs);
        }
    }

    private static File[] getAllFilesInDirectory(File direc) {
        File[] allFiles = direc.listFiles();
        if (allFiles != null && allFiles.length > 0) {
            List<File> toReturn = new ArrayList<>();
            for (File i : allFiles) {
                if (i.isFile()) {
                    toReturn.add(i);
                } else if (i.isDirectory()) {
                    toReturn.addAll(fileToList(getAllFilesInDirectory(i)));
                }
            }
            return toReturn.toArray(new File[0]);
        } else {
            return new File[0];
        }
    }

    private static List<File> fileToList(File[] f) {
        List<File> r = new ArrayList<>(f.length);
        for (int i = 0; i < f.length; i++) {
            r.add(i, f[i]);
        }
        return r;
    }
}