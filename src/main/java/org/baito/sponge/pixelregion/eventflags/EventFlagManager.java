package org.baito.sponge.pixelregion.eventflags;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import org.baito.sponge.pixelregion.Config;
import org.spongepowered.api.entity.living.player.Player;

import java.io.File;
import java.util.*;

public class EventFlagManager {
    public static Map<String, EventFlag> events = new HashMap<>();

    public static void generateEvents(File[] f) {
        for (int i = 0; i < f.length; i++) {
            EventFlag e = new EventFlag(Config.readConfig(f[i]));
            events.put(e.name, e);
        }
    }

    public static EventFlag getFlag(String name) {
        if (events.containsKey(name)) {
            return events.get(name);
        }
        return null;
    }

    public static boolean NBTMatch(NBTTagCompound one, NBTTagCompound two) {
        if (one.getKeySet().containsAll(two.getKeySet())) {
            Set<String> set = two.getKeySet();
            for (String e : set) {
                switch (two.getTagId(e)) {
                    case 1: // BYTE
                        if (two.getByte(e) != one.getByte(e)) return false;
                        break;
                    case 2: // SHORT
                        if (two.getShort(e) != one.getByte(e)) return false;
                        break;
                    case 3: // INT
                        if (two.getInteger(e) != one.getInteger(e)) return false;
                        break;
                    case 4: // LONG
                        if (two.getLong(e) != one.getLong(e)) return false;
                        break;
                    case 5: // FLOAT
                        if (two.getFloat(e) != one.getFloat(e)) return false;
                        break;
                    case 6: // DOUBLE
                        if (two.getDouble(e) != one.getDouble(e)) return false;
                        break;
                    case 7: // BYTE ARRAY
                        if (!Arrays.equals(two.getByteArray(e), one.getByteArray(e))) return false;
                        break;
                    case 8: // STRING
                        if (!two.getString(e).equals(one.getString(e))) return false;
                        break;
                    case 9: // LIST
                        for (int i = 0; i < two.getTagList(e, 9).tagCount(); i++) {
                            if (!NBTMatch(two.getTagList(e, 9).getCompoundTagAt(i), one.getTagList(e, 9).getCompoundTagAt(i))) return false;
                        }
                        break;
                    case 10: // NBT COMPOUND
                        if (!NBTMatch(one.getCompoundTag(e), two.getCompoundTag(e))) return false;
                        break;
                    case 11: // INT ARRAY
                        if (!Arrays.equals(two.getIntArray(e), one.getIntArray(e))) return false;
                        break;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public static boolean metConditions(Player p, EventFlag.FlagCondition f) {
        PlayerFlagData pfd = PlayerFlagDataManager.getOrCreateData(p);
        if (f.enabledFlags != null) {
            for (String i : f.enabledFlags) {
                if (!pfd.flagState(EventFlagManager.getFlag(i))) {
                    return false;
                }
            }
        }
        if (f.disabledFlag != null) {
            for (String i : f.disabledFlag) {
                if (pfd.flagState(EventFlagManager.getFlag(i))) {
                    return false;
                }
            }
        }
        if (f.partyCondition != null) {
            EventFlag.FlagCondition.PartyCondition pc = f.partyCondition;
            PlayerPartyStorage party = Pixelmon.storageManager.getParty(p.getUniqueId());
            if (f.partyCondition.useEntireParty) {
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
        if (f.worldCondition != null) {
            EventFlag.FlagCondition.WorldCondition wc = f.worldCondition;
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

    public static boolean contains(String item, String[] arr) {
        for (String i : arr) {
            if (i.toUpperCase().equals(item.toUpperCase())) return true;
        }
        return false;
    }

    public static boolean contains(String[] arr1, String[] arr2) {
        for (String i : arr1) {
            for (String e : arr2) {
                if (e.toUpperCase().equals(i.toUpperCase())) return true;
            }
        }
        return false;
    }

    public static boolean equals(String one, String two) {
        return one.toUpperCase().equals(two.toUpperCase());
    }

    public static boolean shiny(List<Pokemon> e) {
        for (Pokemon i : e) {
            if (i.isShiny()) return true;
        }
        return false;
    }
}
