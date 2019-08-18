package org.baito.sponge.pixelregion.encounterdata.external;

import com.pixelmonmod.pixelmon.api.events.ExternalMoveEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.json.JSONArray;
import org.json.JSONObject;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.living.player.Player;

public class ForageData {
    public String name;
    public int chance;
    public ForageConditions[] conditions;
    public ForageItems[] items;

    public boolean metConditions(Player plr, ExternalMoveEvent.ForageMove e) {
        if (conditions == null) return true;
        for (ForageConditions i : conditions) {
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
                    if (!contains(i.blocks, (((BlockState) e.pokemon.world.getBlockState(e.getTarget().getBlockPos())).getType().getName())))
                        return false;
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

    private boolean contains(String[] arr, String s) {
        for (String i : arr) {
            if (i.equals(s)) return true;
        }
        return false;
    }

    public ForageItems getForageItem() {
        double totalWeight = 0;
        for (ForageItems i : items) {
            totalWeight += i.weight;
        }
        double chosenWeight = Math.random() * totalWeight;
        for (ForageItems i : items) {
            chosenWeight -= i.weight;
            if (chosenWeight <= 0.0) return i;
        }
        return null;
    }

    public ForageData(JSONObject j) {
        try {
            if (!j.has("name")) {
                throw new NullPointerException("A forage data has no name! Skipping...");
            }
            name = j.getString("name");
            if (!j.has("chance")) {
                throw new NullPointerException("Forage data \"" + name + "\" has no chance! Skipping...");
            }
            chance = j.getInt("chance");
            if (j.has("conditions")) {
                conditions = new ForageConditions[j.getJSONArray("conditions").length()];
                for (int i = 0; i < conditions.length; i++) {
                    conditions[i] = new ForageConditions(j.getJSONArray("conditions").getJSONObject(i), name);
                }
            } else {
                conditions = null;
            }
            if (!j.has("loot")) {
                throw new NullPointerException("Forage data \"" + name + "\" has no loot array! Skipping...");
            }
            items = new ForageItems[j.getJSONArray("loot").length()];
            for (int i = 0; i < j.getJSONArray("loot").length(); i++) {
                items[i] = new ForageItems(j.getJSONArray("loot").getJSONObject(i), name);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public class ForageConditions {
        public String type;
        public String[] weather = null;
        public int[] time = null;
        public String[] blocks = null;
        public String[] types = null;

        ForageConditions(JSONObject j, String name) {
            try {
                if (!j.has("type")) {
                    throw new NullPointerException("Forage data " + name + " is missing a condition type! Skipping...");
                }
                type = j.getString("type");
                switch (j.getString("type")) {
                    case "weather":
                        if (!j.has("weather")) {
                            throw new NullPointerException("Forage data " + name + " has no \"weather\" array for condition weather! Skipping...");
                        }
                        weather = toArray(j.getJSONArray("weather"));
                        break;
                    case "time":
                        if (!j.has("times")) {
                            throw new NullPointerException("Forage data " + name + " has no \"times\" array for condition time! Skipping...");
                        }
                        time = new int[2];
                        time[0] = j.getJSONArray("times").getInt(0);
                        time[1] = j.getJSONArray("times").getInt(1);
                        break;
                    case "blocks":
                        if (!j.has("blocks")) {
                            throw new NullPointerException("Forage data " + name + " has no \"blocks\" array for condition blocks! Skipping...");
                        }
                        blocks = toArray(j.getJSONArray("blocks"));
                        break;
                    case "types":
                        if (!j.has("types")) {
                            throw new NullPointerException("Forage data " + name + " has no \"types\" array for condition types! Skipping...");
                        }
                        types = toArray(j.getJSONArray("types"));
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
    }

    public class ForageItems {
        public ItemStack item;
        public double weight;

        ForageItems(JSONObject j, String name) {
            if (Item.getByNameOrId(j.getString("item")) == null) {
                throw new NullPointerException("An item in forage data \"" + name + "\" is incorrect! Skipping...");
            } else {
                item = new ItemStack(Item.getByNameOrId(j.getString("item")), 1);
                weight = j.getDouble("weight");
            }
        }
    }
}
