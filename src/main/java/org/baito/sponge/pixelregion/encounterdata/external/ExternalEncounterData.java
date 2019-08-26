package org.baito.sponge.pixelregion.encounterdata.external;

import com.pixelmonmod.pixelmon.api.events.ExternalMoveEvent;
import org.baito.sponge.pixelregion.encounterdata.EncounterData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.World;

public class ExternalEncounterData {
    public String name;
    public int chance;
    public ExternalConditions[] conditions;
    public EncounterData.Encounters encounterData;

    public ExternalEncounterData(JSONObject j) {
        try {
            if (!j.has("name")) {
                throw new NullPointerException("An external encounter data has no name! Skipping...");
            }
            name = j.getString("name");
            if (!j.has("chance")) {
                throw new NullPointerException("External encounter data \"" + name + "\" has no chance! Skipping...");
            }
            chance = j.getInt("chance");
            if (!j.has("encounters")) {
                throw new NullPointerException("Extenral encounter data \"" + name + "\" has no encounters! Skipping...");
            }
            if (j.has("conditions")) {
                conditions = new ExternalConditions[j.getJSONArray("conditions").length()];
                for (int i = 0; i < conditions.length; i++) {
                    conditions[i] = new ExternalConditions(j.getJSONArray("conditions").getJSONObject(i), name);
                }
            } else {
                conditions = null;
            }
            encounterData = new EncounterData.Encounters(j.getJSONObject("encounters"), name);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public EncounterData.Encounters.DeepEncounterData getDED() {
        double totalWeight = 0;
        for (EncounterData.Encounters.DeepEncounterData i : encounterData.deepEncounters) {
            totalWeight += i.weight;
        }
        double chosenWeight = Math.random() * totalWeight;
        for (EncounterData.Encounters.DeepEncounterData i : encounterData.deepEncounters) {
            chosenWeight -= i.weight;
            if (chosenWeight <= 0.0) return i;
        }
        return null;
    }

    public class ExternalConditions {
        public String type;
        public String[] weather = null;
        public int[] time = null;
        public String[] blocks = null;

        ExternalConditions(JSONObject j, String name) {
            try {
                if (!j.has("type")) {
                    throw new NullPointerException("Encounter data " + name + " is missing a condition type! Skipping...");
                }
                type = j.getString("type");
                switch (type) {
                    case "weather":
                        if (!j.has("weather")) {
                            throw new NullPointerException("Encounter data " + name + " has no \"weather\" array for condition weather! Skipping...");
                        }
                        weather = toArray(j.getJSONArray("weather"));
                        break;
                    case "time":
                        if (!j.has("times")) {
                            throw new NullPointerException("Encounter data " + name + " has no \"times\" array for condition time! Skipping...");
                        }
                        time = new int[2];
                        time[0] = j.getJSONArray("times").getInt(0);
                        time[1] = j.getJSONArray("times").getInt(1);
                        break;
                    case "blocks":
                        if (!j.has("blocks")) {
                            throw new NullPointerException("Encounter data " + name + " has no \"blocks\" array for condition blocks! Skipping...");
                        }
                        blocks = toArray(j.getJSONArray("blocks"));
                        break;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        private String[] toArray(JSONArray a) {
            String[] returner = new String[a.length()];
            for (int i = 0; i < returner.length; i++) {
                returner[i] = a.getString(i);
            }
            return returner;
        }

        public String[] getDataPrint() {
            switch (type) {
                case "weather":
                    return weather.clone();
                case "time":
                    String[] r = {time[0] + "", time[1] + ""};
                    return r;
                case "blocks":
                    return blocks.clone();
            }
            return null;
        }

        public String getTypePrint() {
            switch (type) {
                case "weather":
                    return "Weather";
                case "time":
                    return "Time";
                case "blocks":
                    return "Blocks";
            }
            return null;
        }
    }

    public boolean metConditions(Player plr, ExternalMoveEvent.PreparingMove e, boolean headButt, World world) {
        if (conditions == null) return true;
        for (ExternalConditions i : conditions) {
            switch (i.type) {
                case "weather":
                    for (String w : i.weather) {
                        if (!contains(i.weather, world.getWeather().getName())) return false;
                    }
                    break;
                case "time":
                    int cTime = ((int) world.getProperties().getWorldTime() % 24000);
                    if (!(cTime >= i.time[0] && cTime <= i.time[1])) return false;
                    break;
                case "blocks":
                    if (!headButt) continue;
                    if (!contains(i.blocks, (((BlockState) e.pokemon.world.getBlockState(e.getTarget().getBlockPos())).getType().getName())))
                        return false;
                    break;
            }
        }
        return true;
    }

    private boolean contains(String[] arr, String s) {
        for (String i : arr) {
            if (i.equals(s)) return true;
        }
        return false;
    }
}
