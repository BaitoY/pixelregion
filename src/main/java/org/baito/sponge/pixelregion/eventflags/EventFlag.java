package org.baito.sponge.pixelregion.eventflags;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.baito.sponge.pixelregion.Utils;
import org.json.JSONObject;
import org.spongepowered.api.block.BlockState;

public class EventFlag {
    public String name;
    public FlagCondition condition;
    public FlagTrigger trigger;
    public FlagEffect effects;

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
            if (j.has("effects")) {
                    effects = new FlagEffect(j.getJSONObject("effects"));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public class FlagCondition {
        public String[] enabledFlags;
        public String[] disabledFlags;
        public PartyCondition partyCondition;
        public WorldCondition worldCondition;

        FlagCondition(JSONObject j) {
            partyCondition = j.has("partyConditions") ? new PartyCondition(j.getJSONObject("partyConditions")) : null;
            worldCondition = j.has("worldConditions") ? new WorldCondition(j.getJSONObject("worldConditions")) : null;
            try {
                if (j.has("flagConditions")) {
                    JSONObject fc = j.getJSONObject("flagConditions");
                    if (fc.has("disabled")) {
                        disabledFlags = fc.getJSONArray("disabled").toList().toArray(new String[0]);
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
            public BlockState[] BLOCKS;
            public boolean onBlock;
            public boolean useVar;

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
                    BLOCKS = new BlockState[j.getJSONArray("inBlocks").length()];
                    for (int i = 0; i < BLOCKS.length; i++) {
                        BLOCKS[i] = Utils.stringToBlock(j.getJSONArray("inBlocks").getString(i));
                    }
                    onBlock = false;
                    useVar = j.has("useVariant") && j.getBoolean("useVariant");
                } else if (j.has("onBlocks")) {
                    BLOCKS = new BlockState[j.getJSONArray("onBlocks").length()];
                    for (int i = 0; i < BLOCKS.length; i++) {
                        BLOCKS[i] = Utils.stringToBlock(j.getJSONArray("onBlocks").getString(i));
                    }
                    onBlock = true;
                    useVar = j.has("useVariant") && j.getBoolean("useVariant");
                } else {
                    BLOCKS = null;
                }
            }
        }

    }

    public class FlagTrigger {
        public TriggerEnum mode;

        public String interactItem;
        public NBTTagCompound nbt;
        public boolean consumeOnUse;

        public String interactBlock;

        FlagTrigger(JSONObject j) {
            try {
                if (j.has("useItem")) {
                    mode = TriggerEnum.ITEM;
                    interactItem = j.getJSONObject("useItem").getString("id");
                    nbt = j.getJSONObject("useItem").has("nbt") ? JsonToNBT.getTagFromJson(j.getJSONObject("useItem").getString("nbt")) : null;
                    consumeOnUse = j.getJSONObject("useItem").has("consume") && j.getJSONObject("useItem").getBoolean("consume");
                } else if (j.has("interact")) {
                    mode = TriggerEnum.INTERACT;
                } else if (j.has("interactBlock")) {
                    mode = TriggerEnum.INTERACTBLOCK;
                    nbt = j.getJSONObject("interactBlock").has("nbt") ? JsonToNBT.getTagFromJson(j.getJSONObject("interactBlock").getString("nbt")) : null;
                    interactBlock = j.getJSONObject("interactBlock").getString("id");
                }
            } catch (NBTException e) {
                e.printStackTrace();
            }
        }

    }

    public class FlagEffect {
        public String[] enableFlags;
        public String[] disableFlags;
        public String[] toggleFlags;
        public EncounterInfo battle;
        public EncounterInfo spawn;
        public ItemStack item;
        public String[] moveTeach;
        public String[] editPoke;
        public String[] evolvePoke;
        public String[] command;

        FlagEffect(JSONObject j) {
            try {
                enableFlags = j.has("enableFlags") ? j.getJSONArray("enableFlags").toList().toArray(new String[0]) : null;
                disableFlags = j.has("disableFlags") ? j.getJSONArray("disableFlags").toList().toArray(new String[0]) : null;
                toggleFlags = j.has("toggleFlags") ? j.getJSONArray("toggleFlags").toList().toArray(new String[0]) : null;
                battle = j.has("battle") ? new EncounterInfo(j.getJSONObject("battle")) : null;
                if (j.has("spawn")) {
                    spawn = j.has("spawn") ? new EncounterInfo(j.getJSONObject("spawn")) : null;
                    spawn.x = j.getJSONObject("spawn").getInt("x");
                    spawn.y = j.getJSONObject("spawn").getInt("y");
                    spawn.z = j.getJSONObject("spawn").getInt("z");
                }
                if (j.has("giveItem")) {
                    JSONObject itemInfo = j.getJSONObject("giveItem");
                    if (!itemInfo.has("id")) {
                        throw new NullPointerException("An event effect has no ID for giveItem! Skipping...");
                    }
                    if (Item.getByNameOrId(itemInfo.getString("id")) == null) {
                        throw new NullPointerException("An event effect's ID for giveItem " + itemInfo.getString("id") + " does not exist! Skipping");
                    }
                    if (itemInfo.has("quantity")) {
                        item = new ItemStack(Item.getByNameOrId(itemInfo.getString("id")), itemInfo.getInt("quantity"));
                    } else {
                        item = new ItemStack(Item.getByNameOrId(itemInfo.getString("id")), 1);
                    }
                    try {
                        if (itemInfo.has("nbt")) {
                            NBTTagCompound nbt = JsonToNBT.getTagFromJson(itemInfo.getString("nbt"));
                            item.setTagCompound(nbt);
                        }
                    } catch (NBTException ex) {
                        ex.printStackTrace();
                    }
                }
                if (j.has("teachMove")) {
                    JSONObject tm = j.getJSONObject("teachMove");
                    moveTeach = new String[2];
                    if (!tm.has("pokemon") && !tm.has("move")) {
                        throw new NullPointerException("An event effect has no Pokemon or move for teachMove! Skipping...");
                    }
                    moveTeach[0] = tm.getString("pokemon");
                    moveTeach[1] = tm.getString("move");
                }
                if (j.has("modifyPokemon")) {
                    editPoke = new String[2];
                    JSONObject tm = j.getJSONObject("modifyPokemon");
                    if (!tm.has("pokemon") && !tm.has("spec")) {
                        throw new NullPointerException("An event effect has no Pokemon or spec for modifyPokemon! Skipping...");
                    }
                    editPoke[0] = tm.getString("pokemon");
                    editPoke[1] = tm.getString("spec");
                }
                command = j.has("runCommand") ? j.getJSONArray("runCommand").toList().toArray(new String[0]) : null;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

    }
}