package org.baito.sponge.pixelregion.eventflags;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.json.JSONObject;

public class EventFlag {
    public String name;
    public FlagCondition condition;
    public FlagTrigger trigger;

    EventFlag(JSONObject j) {
        try {
            if (!j.has("name")) {
                throw new NullPointerException("An Event has no \"name\"! Skipping...");
            }
            name = j.getString("name");
            if (j.has("conditions")) {
                condition = new FlagCondition(j.getJSONObject("conditions"));
            }
            if (j.has("trigger")) {
                trigger = new FlagTrigger(j.getJSONObject("trigger"));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public class FlagCondition {
        public String[] enabledFlags;
        public String[] disabledFlag;
        public PartyCondition partyCondition;
        public WorldCondition worldCondition;

        FlagCondition(JSONObject j) {
            partyCondition = j.has("partyConditions") ? new PartyCondition(j.getJSONObject("partyConditions")) : null;
            worldCondition = j.has("worldConditions") ? new WorldCondition(j.getJSONObject("worldConditions")) : null;
            try {
                if (j.has("flagConditions")) {
                    JSONObject fc = j.getJSONObject("flagConditions");
                    if (fc.has("disabled")) {
                        disabledFlag = fc.getJSONArray("disabled").toList().toArray(new String[0]);
                    }
                    if (fc.has("enabled")) {
                        enabledFlags = fc.getJSONArray("enabled").toList().toArray(new String[0]);
                    }
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        public class PartyCondition {
            public boolean useEntireParty;
            public String SPECIES;
            public int AVERAGE = -1;
            public String[] TYPES;
            public String ABILITY;
            public String MOVE;
            public boolean SHINY;
            public String GENDER;
            public String HELDITEM;

            PartyCondition(JSONObject j) {
                try {
                    if (!j.has("useEntireParty")) {
                        throw new NullPointerException("An Event Flag's party condition " +
                                "has no \"useEntireParty\" boolean! Skipping...");
                    }
                    useEntireParty = j.getBoolean("useEntireParty");
                    SPECIES = j.has("species") ? j.getString("species") : null;
                    AVERAGE = j.has("average") ? Math.max(Math.min(j.getInt("average"), 100), 1) : -1;
                    TYPES = j.has("types") ? j.getJSONArray("types").toList().toArray(new String[0]) : null;
                    ABILITY = j.has("ability") ? j.getString("ability") : null;
                    MOVE = j.has("move") ? j.getString("move") : null;
                    SHINY = j.has("shiny") && j.getBoolean("shiny");
                    GENDER = j.has("gender") ? j.getString("gender") : null;
                    HELDITEM = j.has("heldItem") ? j.getString("heldItem") : null;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

        public class WorldCondition {
            public int[] TIME;
            public String[] WEATHER;
            public String[] BLOCKS;
            public boolean onBlock;

            WorldCondition(JSONObject j) {
                if (j.has("times")) {
                    TIME = new int[2];
                    TIME[0] = j.getJSONArray("times").getInt(0);
                    TIME[1] = j.getJSONArray("times").getInt(1);
                }
                if (j.has("weather")) {
                    WEATHER = j.getJSONArray("weather").toList().toArray(new String[0]);
                }
                if (j.has("inBlocks")) {
                    BLOCKS = j.getJSONArray("inBlocks").toList().toArray(new String[0]);
                    onBlock = false;
                } else if (j.has("onBlocks")) {
                    BLOCKS = j.getJSONArray("onBlocks").toList().toArray(new String[0]);
                    onBlock = true;
                } else {
                    BLOCKS = null;
                }
            }
        }

    }

    public class FlagTrigger {
        public String interactItem;
        public NBTTagCompound nbt;

        public TriggerEnum mode;

        FlagTrigger(JSONObject j) {
            try {
                if (j.has("useItem")) {
                    mode = TriggerEnum.ITEM;
                    interactItem = j.getJSONObject("useItem").getString("id");
                    nbt = JsonToNBT.getTagFromJson(j.getJSONObject("useItem").getString("nbt"));
                } else if (j.has("interact")) {
                    mode = TriggerEnum.INTERACT;
                } else if (j.has("interactBlock")) {
                    mode = TriggerEnum.INTERACTBLOCK;
                }
            } catch (NBTException e) {
                e.printStackTrace();
            }
        }

    }

}

