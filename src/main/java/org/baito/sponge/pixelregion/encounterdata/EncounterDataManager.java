package org.baito.sponge.pixelregion.encounterdata;

import com.pixelmonmod.pixelmon.api.events.ExternalMoveEvent;
import org.baito.sponge.pixelregion.Config;
import org.baito.sponge.pixelregion.encounterdata.external.ExternalEncounterData;
import org.baito.sponge.pixelregion.encounterdata.external.ForageData;
import org.baito.sponge.pixelregion.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.living.player.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EncounterDataManager {
    public static Map<String, EncounterData> encounterData = new HashMap<>();
    public static Map<String, ExternalEncounterData> externalEncounterData = new HashMap<>();
    public static Map<String, ForageData> forageData = new HashMap<>();

    public static void generateEncounters(File[] f) {
        encounterData .clear();
        for (int i = 0; i < f.length; i++) {
            EncounterData ed = new EncounterData(Config.readConfig(f[i]));
            encounterData.put(ed.name, ed);
        }
    }

    public static void generateExtEncounters(File[] f) {
        externalEncounterData.clear();
        for (int i = 0; i < f.length; i++) {
            ExternalEncounterData ed = new ExternalEncounterData(Config.readConfig(f[i]));
            externalEncounterData.put(ed.name, ed);
        }
    }

    public static void generateForageData(File[] f) {
        forageData.clear();
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

    public static boolean metConditions(Player plr, EncounterData c) {
        if (c.conditions == null) return true;
        for (EncounterData.Conditions i : c.conditions) {
            switch (i.type) {
                case "weather":
                    for (String w : i.weather) {
                        if (!contains(i.weather, plr.getWorld().getWeather().getName())) return false;
                    }
                    break;
                case "time":
                    int cTime = ((int) plr.getWorld().getProperties().getWorldTime() % 24000);
                    if (!(cTime >= i.time[0] && cTime <= i.time[1])) return false;
                    break;
                case "ontop":
                    BlockState blockState = plr.getWorld().getBlock(plr.getPosition().toInt().add(0, -1, 0));
                    if (i.useVar) {
                        if (!Utils.matches(blockState, i.ontop)) return false;
                    } else {
                        if (!Utils.matches(blockState.getType(), i.ontop)) return false;
                    }
                    break;
                case "inside":
                    blockState = plr.getWorld().getBlock(plr.getPosition().toInt().add(0, 0, 0));
                    if (i.useVar) {
                        if (!Utils.matches(blockState, i.inside)) return false;
                    } else {
                        if (!Utils.matches(blockState.getType(), i.inside)) return false;
                    }
                    break;
            }
        }
        return true;
    }

    public static boolean metConditions(Player plr, ExternalMoveEvent.PreparingMove e, boolean headButt, ExternalEncounterData ext) {
        if (ext.conditions == null) return true;
        for (ExternalEncounterData.ExternalConditions i : ext.conditions) {
            switch (i.type) {
                case "weather":
                    for (String w : i.weather) {
                        if (!contains(i.weather, plr.getWorld().getWeather().getName())) return false;
                    }
                    break;
                case "time":
                    int cTime = ((int) plr.getWorld().getProperties().getWorldTime() % 24000);
                    if (!(cTime >= i.time[0] && cTime <= i.time[1])) return false;
                    break;
                case "blocks":
                    if (!headButt) continue;
                    BlockState b = ((BlockState)e.pokemon.world.getBlockState(e.getTarget().getBlockPos()));
                    if (i.useVar) {
                        if (!Utils.matches(b, i.blocks)) return false;
                    } else {
                        if (!Utils.matches(b.getType(), i.blocks)) return false;
                    };
                    break;
            }
        }
        return true;
    }

    public static boolean metConditions(Player plr, ExternalMoveEvent.ForageMove e, ForageData fd) {
        if (fd.conditions == null) return true;
        for (ForageData.ForageConditions i : fd.conditions) {
            switch (i.type) {
                case "weather":
                    for (String w : i.weather) {
                        if (!contains(i.weather, Sponge.getServer().getWorld(Sponge.getServer().
                                getDefaultWorldName()).get().getWeather().getName())) return false;
                    }
                    break;
                case "time":
                    int cTime = ((int) Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get().getProperties().getWorldTime() % 24000);
                    if (!(cTime >= i.time[0] && cTime <= i.time[1])) return false;
                    break;
                case "blocks":
                    BlockState b = ((BlockState)e.pokemon.world.getBlockState(e.getTarget().getBlockPos()));
                    if (i.useVar) {
                        if (!Utils.matches(b, i.blocks)) return false;
                    } else {
                        if (!Utils.matches(b.getType(), i.blocks)) return false;
                    };
                    break;
                case "types":
                    if (!contains(i.types, e.pokemon.getBaseStats().types.get(0).toString().toLowerCase())) {
                        if (e.pokemon.getBaseStats().types.size() == 2) {
                            if (!contains(i.types, e.pokemon.getBaseStats().types.get(1).toString().toLowerCase())) {
                                return false;
                            } else {
                                break;
                            }
                        } else {
                            return false;
                        }
                    } else {
                        break;
                    }
            }
        }
        return true;
    }

    public static boolean contains(String[] arr, String s) {
        for (String i : arr) {
            if (i.equals(s)) return true;
        }
        return false;
    }
}
