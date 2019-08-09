package org.baito.sponge.pixelregion.encounterdata;

import com.pixelmonmod.pixelmon.Pixelmon;
import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

public class EncounterData {
    public String name;
    public int tickChance;
    public Conditions[] conditions;
    public Encounters encounterData;

    EncounterData(JSONObject j) {
        try {
            if (!j.has("name")) {
                throw new NullPointerException("An encounter data has no name! Skipping...");
            }
            name = j.getString("name");
            if (!j.has("tickChance")) {
                throw new NullPointerException("Encounter data \"" + name + "\" has no tick chance! Skipping...");
            }
            tickChance = j.getInt("tickChance");
            if (j.has("conditions")) {
                conditions = new Conditions[j.getJSONArray("conditions").length()];
                for (int i = 0; i < conditions.length; i++) {
                    conditions[i] = new Conditions(j.getJSONArray("conditions").getJSONObject(i), name);
                }
            } else {
                conditions = null;
            }
            if (!j.has("encounters")) {
                throw new NullPointerException("Encounter data \"" + name + "\" has no encounters! Skipping...");
            }
            encounterData = new Encounters(j.getJSONObject("encounters"), name);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public boolean condMet(Player plr) {
        for (Conditions i : conditions) {
            if (!i.isTrue(plr)) return false;
        }
        return true;
    }

    public Encounters.DeepEncounterData getDED() {
        double totalWeight = 0;
        for (Encounters.DeepEncounterData i : encounterData.deepEncounters) {
            totalWeight += i.weight;
        }
        int ind;
        double chosenWeight = Math.random() * totalWeight;
        for (Encounters.DeepEncounterData i : encounterData.deepEncounters) {
            chosenWeight -= i.weight;
            if (chosenWeight <= 0.0) return i;
        }
        return null;
    }

    public class Conditions {
        public String type;
        public String[] weather = null;
        public int[] time = null;
        public String[] onBlocks = null;
        public String[] inBlocks = null;

        Conditions(JSONObject j, String name) {
            try {
                if (!j.has("type")) {
                    throw new NullPointerException("Encounter data " + name + " is missing a condition type! Skipping...");
                }
                type = j.getString("type");
                switch (j.getString("type")) {
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
                    case "onBlocks":
                        if (!j.has("blocks")) {
                            throw new NullPointerException("Encounter data " + name + " has no \"blocks\" array for condition onBlocks! Skipping...");
                        }
                        onBlocks = new String[j.getJSONArray("blocks").length()];
                        for (int i = 0; i < j.getJSONArray("blocks").length(); i++) {
                            onBlocks[i] = j.getJSONArray("blocks").getString(i);
                        }
                        break;
                    case "inBlocks":
                        if (!j.has("blocks")) {
                            throw new NullPointerException("Encounter data " + name + " has no \"blocks\" array for condition inBlocks! Skipping...");
                        }
                        inBlocks = new String[j.getJSONArray("blocks").length()];
                        for (int i = 0; i < j.getJSONArray("blocks").length(); i++) {
                            inBlocks[i] = j.getJSONArray("blocks").getString(i);
                        }
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
                case "onBlocks":
                    return onBlocks.clone();
                case "inBlocks":
                    return inBlocks.clone();
            }
            return null;
        }

        public String getTypePrint() {
            switch (type) {
                case "weather":
                    return "Weather";
                case "time":
                    return "Time";
                case "onBlocks":
                    return "On Blocks";
                case "inBlocks":
                    return "In Blocks";
            }
            return null;
        }

        public Boolean isTrue(Player plr) {
            switch (type) {
                case "weather":
                    Weather current = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get().getWeather();
                    String currentS;
                    if (Weathers.CLEAR.equals(current)) {
                        currentS = "clear";
                    } else if (Weathers.RAIN.equals(current)) {
                        currentS = "rain";
                    } else {
                        currentS = "thunder";
                    }
                    for (int i = 0; i < weather.length; i++) {
                        if (weather[i].toUpperCase().equals(currentS)) return true;
                    }
                    break;
                case "time":
                    int cTime = (int) (Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get().getProperties().getWorldTime() % 24000);
                    if (cTime >= time[0] && cTime <= time[1]) return true;
                    break;
                case "onBlocks":
                    BlockState blockUnder = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName())
                            .get().getBlock(plr.getPosition().getFloorX(), plr.getPosition().getFloorY() - 1,
                                    plr.getPosition().getFloorZ());
                    for (String i : onBlocks) {
                        if (i.equals(blockUnder.getId())) return true;
                    }
                    break;
                case "inBlocks":
                    BlockState blockIn = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName())
                            .get().getBlock(plr.getPosition().getFloorX(), plr.getPosition().getFloorY(),
                                    plr.getPosition().getFloorZ());
                    for (String i : onBlocks) {
                        if (i.equals(blockIn.getId())) return true;
                    }
                    break;
            }
            return false;
        }
    }

    public class Encounters {
        public int[] defaultLevels = new int[2];
        public int defaultShiny;
        public int defaultBoss;
        public DeepEncounterData[] deepEncounters;

        Encounters(JSONObject j, String name) {
            try {
                if (!j.has("levelMin") || !j.has("levelMax")) {
                    throw new NullPointerException("Encounter data " + name + " has no global default levels! Skipping...");
                }
                defaultLevels[0] = j.has("levelMin") ? j.getInt("levelMin") : null;
                defaultLevels[1] = j.has("levelMax") ? j.getInt("levelMax") : null;
                if (!j.has("shinyChance")) {
                    throw new NullPointerException("Encounter data " + name + " has no global default shiny chance! Skipping...");
                }
                defaultShiny = j.has("shinyChance") ? j.getInt("shinyChance") : null;
                if (!j.has("bossChance")) {
                    throw new NullPointerException("Encounter data " + name + " has no global default boss chance! Skipping...");
                }
                defaultBoss = j.has("bossChance") ? j.getInt("bossChance") : null;
                if (!j.has("pokemon")) {
                    throw new NullPointerException("Encounter data " + name + " has no Pokemon encounters! Skipping...");
                }
                deepEncounters = new DeepEncounterData[j.getJSONArray("pokemon").length()];
                for (int i = 0; i < deepEncounters.length; i++) {
                    deepEncounters[i] = new DeepEncounterData(j.getJSONArray("pokemon").getJSONObject(i), this, name);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        public class DeepEncounterData {
            public String[] species;
            public double weight;
            public int[] deepLevels = new int[2];
            public int deepShiny;
            public int deepBoss;

            DeepEncounterData(JSONObject j, Encounters def, String name) {
                try {
                    if (!j.has("species")) {
                        throw new NullPointerException("Encounter data " + name + " has no species in a Pokemon field! Skipping...");
                    }
                    species = j.has("species") ? toArray(j.getJSONArray("species")) : null;
                    if (!j.has("weight")) {
                        throw new NullPointerException("Encounter data " + name + " has no weight in a Pokemon field! Skipping...");
                    }
                    weight = j.has("weight") ? (double) j.getNumber("weight") : null;
                    deepLevels[0] = j.has("levelMin") ? j.getInt("levelMin") : def.defaultLevels[0];
                    deepLevels[1] = j.has("levelMax") ? j.getInt("levelMax") : def.defaultLevels[1];
                    deepShiny = j.has("shinyChance") ? j.getInt("shinyChance") : def.defaultShiny;
                    deepBoss = j.has("bossChance") ? j.getInt("bossChance") : def.defaultBoss;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            private String[] toArray(JSONArray a) {
                String[] r = new String[a.length()];
                for (int i = 0; i < a.length(); i++) {
                    r[i] = a.getString(i);
                }
                return r;
            }

            public void execute(Player plr) {
                StringBuilder sb = new StringBuilder();
                sb.append(species[(int)Math.floor(Math.random() * species.length)]);
                sb.append(",lvl:"+((int)Math.floor(Math.random() * deepLevels[1] - deepLevels[0]) + deepLevels[0]));
                int shinyBonus = 0;
                if (Pixelmon.storageManager.getParty(plr.getUniqueId()).getShinyCharm().isActive()) {
                    shinyBonus = 3;
                }
                if (Math.floor(Math.random() * deepShiny + shinyBonus) == 0) {
                    sb.append(",s");
                } else {
                    sb.append(",!s");
                }
                if (Math.floor(Math.random() * deepBoss) == 0) {
                    sb.append(",boss:" + Math.floor(Math.random() * 3) + 1);
                }
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), "pokebattle " + plr.getName() + " " + sb.toString());
            }
        }
    }

    public String info() {
        StringBuilder s = new StringBuilder();
        s.append("\n  &a= Encounter chance: &f").append(tickChance).append("%");
        s.append("\n  &a= Conditions:");
        for (int cond = 0; cond < conditions.length; cond++) {
            s.append("\n    &b= ").append(conditions[cond].getTypePrint()).append(": ");
            s.append("\n      &3- ");
            for (int dp = 0; dp < conditions[cond].getDataPrint().length; dp++) {
                String dps = conditions[cond].getDataPrint()[dp];
                if (dp == conditions[cond].getDataPrint().length - 1) {
                    s.append("&f").append(dps);
                } else {
                    s.append("&f").append(dps).append(", ");
                }
            }
        }
        for (int pl = 0; pl < encounterData.deepEncounters.length; pl++) {
            s.append("\n  &a= Pokemon: ");
            for (int pkmn = 0; pkmn < encounterData.deepEncounters[pl].species.length; pkmn++) {
                String pokemon = encounterData.deepEncounters[pl].species[pkmn];
                if (pkmn == encounterData.deepEncounters[pl].species.length) {
                    s.append("&f").append(pokemon);
                } else {
                    s.append("&f").append(pokemon).append(", ");
                }
            }
            Encounters.DeepEncounterData dd = encounterData.deepEncounters[pl];
            s.append("\n    &b= Level Range: &f").append(dd.deepLevels[0]).append(" - ").append(dd.deepLevels[1]);
            s.append("\n    &b= Weighting: &f").append(dd.weight);
        }
        return s.toString();
    }
}
