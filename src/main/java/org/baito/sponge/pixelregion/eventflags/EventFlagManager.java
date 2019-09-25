package org.baito.sponge.pixelregion.eventflags;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.dialogue.Dialogue;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.api.storage.StoragePosition;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.attacks.Attack;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.comm.packetHandlers.OpenReplaceMoveScreen;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import org.baito.sponge.pixelregion.Config;
import org.baito.sponge.pixelregion.Utils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventFlagManager {
    public static Map<String, EventFlag> events = new HashMap<>();

    public static void generateEvents(File[] f) {
        events.clear();
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

    public static boolean metConditions(Player p, EventFlag.FlagCondition f) {
        PlayerFlagData pfd = PlayerFlagDataManager.getOrCreateData(p);
        if (f.enabledFlags != null) {
            for (String i : f.enabledFlags) {
                if (pfd.flagState(EventFlagManager.getFlag(i)) != true) {
                    return false;
                }
            }
        }
        if (f.disabledFlags != null) {
            for (String i : f.disabledFlags) {
                if (pfd.flagState(EventFlagManager.getFlag(i)) != false) {
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
                    if (!Utils.equals(pc.SPECIES, slotOne.getSpecies().name)) return false;
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
                    if (!Utils.equals(pc.ABILITY, slotOne.getAbilityName())) return false;
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
                    if (!Utils.equals(slotOne.getGender().toString(), pc.GENDER)) return false;
                }
                if (pc.HELDITEM != null) {
                    if (!Utils.equals(pc.HELDITEM, slotOne.getHeldItem().getItem().delegate.name().toString())) return false;
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
                BlockState blockState;
                if (wc.onBlock) {
                    blockState = p.getWorld().getBlock(p.getPosition().toInt().add(0, -1, 0));
                } else {
                    blockState = p.getWorld().getBlock(p.getPosition().toInt().add(0, 0, 0));
                }
                if (wc.useVar) {
                    if (!Utils.matches(blockState, wc.BLOCKS)) return false;
                } else {
                    if (!Utils.matches(blockState.getType(), wc.BLOCKS)) return false;
                };
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

    public static boolean shiny(List<Pokemon> e) {
        for (Pokemon i : e) {
            if (i.isShiny()) return true;
        }
        return false;
    }

    public static boolean metTrigger(EventFlag.FlagTrigger t, InteractBlockEvent.Secondary ev) {
        if (t.mode != TriggerEnum.INTERACTBLOCK) {
            return false;
        }
        if (t.interactBlock == null) {
            return false;
        }
        if (Utils.stringToBlock(t.interactBlock) != ev.getTargetBlock().getState()) {
            return false;
        }
        return true;
    }

    public static boolean metTrigger(EventFlag.FlagTrigger t, InteractItemEvent.Secondary ev) {
        if (t.mode != TriggerEnum.ITEM) {
            return false;
        }
        if (t.interactItem == null) {
            return false;
        }
        if (!ev.getItemStack().getType().getName().equals(t.interactItem)) {
            return false;
        }
        if (t.nbt != null && !Utils.NBTMatch(t.nbt, ItemStackUtil.toNative(ev.getItemStack().createStack()).serializeNBT())) {
            return false;
        }
        return true;
    }

    public static void runEffects(Player p, EventFlag.FlagEffect e) {
        if (e.enableFlags != null) {
            PlayerFlagData pfd = PlayerFlagDataManager.getOrCreateData(p);
            for (String i : e.enableFlags) {
                pfd.setFlag(i, true);
            }
        }
        if (e.disableFlags != null) {
            PlayerFlagData pfd = PlayerFlagDataManager.getOrCreateData(p);
            for (String i : e.disableFlags) {
                pfd.setFlag(i, false);
            }
        }
        if (e.toggleFlags != null) {
            PlayerFlagData pfd = PlayerFlagDataManager.getOrCreateData(p);
            for (String i : e.toggleFlags) {
                pfd.setFlag(i, !pfd.flagState(i));
            }
        }
        if (e.battle != null) {
            if (BattleRegistry.getBattle((EntityPlayerMP)p) == null) {
                EntityPlayerMP mp = (EntityPlayerMP) p;
                PlayerParticipant pp = new PlayerParticipant(mp, Pixelmon.storageManager.
                        getParty(p.getUniqueId()).getAndSendOutFirstAblePokemon((mp)));
                BattleRegistry.startBattle(pp, new WildPixelmonParticipant(e.battle.spawn(mp)));
            }
        }
        if (e.spawn != null) {
            e.spawn.spawn(p.getWorld());
        }
        if (e.item != null) {
            p.getInventory().offer(ItemStackUtil.fromNative(e.item));
        }
        if (e.moveTeach != null) {
            List<Pokemon> party = Pixelmon.storageManager.getParty(p.getUniqueId()).getTeam();
            for (Pokemon i : party) {
                if (Utils.equals(i.getSpecies().name, e.moveTeach[0])) {
                    if (Attack.hasAttack(e.moveTeach[1])) {
                        Attack toTeach = new Attack(e.moveTeach[1]);
                        if (!i.getMoveset().hasAttack(toTeach)) {
                            Pixelmon.network.sendTo(new OpenReplaceMoveScreen(i.getUUID(), toTeach.getActualMove().getAttackId()), ((EntityPlayerMP)p));
                            break;
                        }
                    }
                }
            }
        }
        if (e.editPoke != null) {
            List<Pokemon> party = Pixelmon.storageManager.getParty(p.getUniqueId()).getTeam();
            for (Pokemon i : party) {
                if (Utils.equals(i.getSpecies().name, e.editPoke[0])) {
                        PokemonSpec.from(e.editPoke[1].split(" ")).apply(i);
                        break;
                }
            }
        }
        if (e.command != null) {
            for (String i : e.command) {
                Sponge.getCommandManager().process(Sponge.getServer().getConsole(), i.replaceAll("\\{player}", p.getName()));
            }
        }
        if (e.message != null) {
            ArrayList<Dialogue> list = new ArrayList<>();
            for (String m : e.message) {
                list.add(new Dialogue("", m, m, null));
            }
            Dialogue.setPlayerDialogueData((EntityPlayerMP) p, list, true);
        }
    }
}
