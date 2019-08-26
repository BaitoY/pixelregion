package org.baito.sponge.pixelregion.eventflags;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import org.json.JSONObject;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

        private class PartyCondition {
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

        private class WorldCondition {
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

        public boolean metConditions(Player p) {
            PlayerFlagData pfd = PlayerFlagDataManager.getOrCreateData(p);
            if (enabledFlags != null) {
                for (String i : enabledFlags) {
                    if (!pfd.flagState(EventFlagManager.getFlag(i))) {
                        return false;
                    }
                }
            }
            if (disabledFlag != null) {
                for (String i : disabledFlag) {
                    if (pfd.flagState(EventFlagManager.getFlag(i))) {
                        return false;
                    }
                }
            }
            if (partyCondition != null) {
                PartyCondition pc = partyCondition;
                PlayerPartyStorage party = Pixelmon.storageManager.getParty(p.getUniqueId());
                if (partyCondition.useEntireParty) {
                    if (pc.SPECIES != null) {
                        List<String> partyNames = new ArrayList<>();
                        for (int i = 0; i < party.getTeam().size(); i++) {
                            partyNames.add(party.getTeam().get(i).getBaseStats().pokemon.name());
                        }
                        if (!contains(pc.SPECIES, partyNames.toArray(new String[0]))) return false;
                    }
                    if (pc.AVERAGE != -1) {
                        if (!(party.getAverageLevel() >= pc.AVERAGE)) return false;
                    }
                    if (pc.TYPES != null) {
                        List<String> types = new ArrayList<>();
                        for (Pokemon i : party.getTeam()) {
                            if (i.getBaseStats().types.size() == 2) {
                                types.add(i.getBaseStats().getType1().toString());
                                types.add(i.getBaseStats().getType1().toString());
                            } else {
                                types.add(i.getBaseStats().getType1().toString());
                            }
                        }
                        if (!contains(types.toArray(new String[0]), pc.TYPES)) return false;
                    }
                    if (pc.ABILITY != null) {
                        List<String> abils = new ArrayList<>();
                        for (Pokemon i : party.getTeam()) {
                            abils.add(i.getAbilityName());
                        }
                        if (!contains(pc.ABILITY, abils.toArray(new String[0]))) return false;
                    }
                    if (pc.MOVE != null) {
                        List<String> moves = new ArrayList<>();
                        for (Pokemon i : party.getTeam()) {
                            for (Attack attack : i.getMoveset()) {
                                moves.add(attack.getActualMove().getAttackName());
                            }
                        }
                        if (!contains(pc.MOVE, moves.toArray(new String[0]))) return false;
                    }
                    if (pc.SHINY) {
                        if (!shiny(party.getTeam())) return false;
                    }
                    if (pc.GENDER != null) {
                        List<String> genders = new ArrayList<>();
                        for (Pokemon i : party.getTeam()) {
                            genders.add(i.getGender().toString());
                        }
                        if (!contains(pc.GENDER, genders.toArray(new String[0]))) return false;
                    }
                    if (pc.HELDITEM != null) {
                        List<String> items = new ArrayList<>();
                        for (Pokemon i : party.getTeam()) {
                            if (!i.getHeldItem().getItem().delegate.name().toString().equals("minecraft:air")) {
                                items.add(i.getHeldItem().getItem().delegate.name().toString());
                            }
                        }
                        if (!contains(pc.HELDITEM, items.toArray(new String[0]))) return false;
                    }
                } else {
                    Pokemon slotOne = Pixelmon.storageManager.getPokemon((EntityPlayerMP) p, new StoragePosition(-1, 0));
                    if (slotOne == null) {
                        return false;
                    }
                    if (pc.SPECIES != null) {
                        if (!equals(pc.SPECIES, slotOne.getSpecies().name)) return false;
                    }
                    if (pc.AVERAGE != -1) {
                        if (!(slotOne.getLevel() >= pc.AVERAGE)) return false;
                    }
                    if (pc.TYPES != null) {
                        if (slotOne.getBaseStats().types.size() == 2) {
                            if (!contains(slotOne.getBaseStats().getType1().getName(), pc.TYPES) &&
                                    !contains(slotOne.getBaseStats().getType2().getName(), pc.TYPES)) return false;
                        } else {
                            if (!contains(slotOne.getBaseStats().getType1().getName(), pc.TYPES)) return false;
                        }
                    }
                    if (pc.ABILITY != null) {
                        if (!equals(pc.ABILITY, slotOne.getAbilityName())) return false;
                    }
                    if (pc.MOVE != null) {
                        List<String> moves = new ArrayList<>();
                        for (Attack attack : slotOne.getMoveset()) {
                            moves.add(attack.getActualMove().getAttackName());
                        }
                        if (!contains(pc.MOVE, moves.toArray(new String[0]))) return false;
                    }
                    if (pc.SHINY) {
                        if (!slotOne.isShiny()) return false;
                    }
                    if (pc.GENDER != null) {
                        if (!equals(slotOne.getGender().toString(), pc.GENDER)) return false;
                    }
                    if (pc.HELDITEM != null) {
                        if (!equals(pc.HELDITEM, slotOne.getHeldItem().getItem().delegate.name().toString())) return false;
                    }
                }
            }
            if (worldCondition != null) {
                WorldCondition wc = worldCondition;
                if (wc.TIME != null) {
                    int cTime = (int) (p.getWorld().getProperties().getWorldTime() % 24000);
                    if (!(cTime >= wc.TIME[0] && cTime <= wc.TIME[1])) return false;
                }
                if (wc.WEATHER != null) {
                    if (!contains(p.getWorld().getWeather().getName(), wc.WEATHER)) return false;
                }
                if (wc.BLOCKS != null) {
                    String block;
                    if (wc.onBlock) {
                        block = p.getWorld().
                                getBlock(p.getPosition().getFloorX(),
                                        p.getPosition().getFloorY() - 1, p.getPosition().getFloorZ()).getType().getName();
                    } else {
                        block = p.getWorld().
                                getBlock(p.getPosition().getFloorX(),
                                        p.getPosition().getFloorY(), p.getPosition().getFloorZ()).getType().getName();
                    }
                    if (!contains(block, wc.BLOCKS)) return false;
                }
            }
            return true;
        }

        private boolean contains(String item, String[] arr) {
            for (String i : arr) {
                if (i.toUpperCase().equals(item.toUpperCase())) return true;
            }
            return false;
        }

        private boolean contains(String[] arr1, String[] arr2) {
            for (String i : arr1) {
                for (String e : arr2) {
                    if (e.toUpperCase().equals(i.toUpperCase())) return true;
                }
            }
            return false;
        }

        private boolean equals(String one, String two) {
            return one.toUpperCase().equals(two.toUpperCase());
        }

        private boolean shiny(List<Pokemon> e) {
            for (Pokemon i : e) {
                if (i.isShiny()) return true;
            }
            return false;
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

        public boolean NBTMatch(NBTTagCompound one, NBTTagCompound two) {
            if (one.getKeySet().containsAll(two.getKeySet())) {
                Set<String> set = two.getKeySet();
                for (String e : set) {
                }
            } else {
                return false;
            }
            return true;
        }

    }

}

