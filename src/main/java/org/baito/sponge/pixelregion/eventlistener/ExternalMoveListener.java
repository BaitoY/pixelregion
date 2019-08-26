package org.baito.sponge.pixelregion.eventlistener;

import com.pixelmonmod.pixelmon.api.events.ExternalMoveEvent;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.storage.playerData.ExternalMoveData;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.baito.sponge.pixelregion.Main;
import org.baito.sponge.pixelregion.encounterdata.EncounterDataManager;
import org.baito.sponge.pixelregion.encounterdata.external.ForageData;
import org.baito.sponge.pixelregion.playerdata.PlayerLink;
import org.baito.sponge.pixelregion.playerdata.PlayerLinkManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;

public class ExternalMoveListener {

    public ExternalMoveListener() {
    }

    @SubscribeEvent
    public void sweetScent(ExternalMoveEvent.PreparingMove e) {
        PlayerLink pl = PlayerLinkManager.getLink((Player) e.player);
        if (pl.inRegion && pl.region != null && pl.region.sweetScentData != null && e.externalMove.getName().equals("sweetscent")) {
            e.setCanceled(true);

            // Gets the External Move used by the Pokemon. Needed so we can get timeLastUsed and the cooldown.
            List<ExternalMoveData> lemd = e.pokemon.getExternalMoveData();
            ExternalMoveData move = null;
            for (ExternalMoveData externalMoveData : lemd) {
                if (externalMoveData.getBaseExternalMove().getName().equals(e.externalMove.getName())) {
                    move = externalMoveData;
                    break;
                }
            }
            // If the total time in the world is greater than
            // the time the move was last used + the cooldown.
            if (e.pokemon.getEntityWorld().getTotalWorldTime() > (move.timeLastUsed + move.getBaseExternalMove().getCooldown(e.pokemon))) {
                e.setCooldown(500 - e.pokemon.getPokemonData().getStat(StatsType.Speed));
                move.timeLastUsed = e.pokemon.world.getTotalWorldTime();
                if (Math.floor(Math.random() * 100) < pl.region.sweetScentData.chance) {
                    if (EncounterDataManager.metConditions((Player) e.player, e, false, pl.region.sweetScentData)) {
                        pl.region.sweetScentData.getDED().execute((Player) e.player);
                    }
                } else {
                    ((Player) e.player).sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Main.prefix + "No Pokemon appeared!"));
                }
            } else {
                long cd = (e.pokemon.world.getTotalWorldTime() - (move.timeLastUsed + move.getBaseExternalMove().getCooldown(e.pokemon))) / 20 * -1;
                ((Player) e.player).sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Main.prefix + "Sweet Scent is cooling down, please wait " + cd + " more seconds!"));
            }
        }
    }

    @SubscribeEvent
    public void headbutt(ExternalMoveEvent.PreparingMove e) {
        PlayerLink pl = PlayerLinkManager.getLink((Player) e.player);
        if (pl.inRegion && pl.region != null && pl.region.headbuttData != null && e.externalMove.getName().equals("headbutt")) {
            e.setCanceled(true);

            // Gets the External Move used by the Pokemon. Needed so we can get timeLastUsed and the cooldown.
            List<ExternalMoveData> lemd = e.pokemon.getExternalMoveData();
            ExternalMoveData move = null;
            for (ExternalMoveData externalMoveData : lemd) {
                if (externalMoveData.getBaseExternalMove().getName().equals(e.externalMove.getName())) {
                    move = externalMoveData;
                    break;
                }
            }

            // If the total time in the world is greater than
            // the time the move was last used + the cooldown.
            if (e.pokemon.getEntityWorld().getTotalWorldTime() > (move.timeLastUsed + move.getBaseExternalMove().getCooldown(e.pokemon))) {
                e.setCooldown(500 - e.pokemon.getPokemonData().getStat(StatsType.Speed));
                move.timeLastUsed = e.pokemon.world.getTotalWorldTime();
                if (Math.floor(Math.random() * 100) < pl.region.headbuttData.chance) {
                    if (EncounterDataManager.metConditions((Player) e.player, e, true, pl.region.headbuttData)) {
                        pl.region.headbuttData.getDED().execute((Player) e.player);
                    }
                } else {
                    ((Player) e.player).sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Main.prefix + "No Pokemon appeared!"));
                }
            } else {
                long cd = (e.pokemon.world.getTotalWorldTime() - (move.timeLastUsed + move.getBaseExternalMove().getCooldown(e.pokemon))) / 20 * -1;
                ((Player) e.player).sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Main.prefix + "Headbutt is cooling down, please wait " + cd + " more seconds!"));
            }
        }
    }

    @SubscribeEvent
    public void forage(ExternalMoveEvent.ForageMove e) {
        PlayerLink pl = PlayerLinkManager.getLink((Player) e.player);
        if (pl.inRegion && pl.region != null && pl.region.forageData != null) {
            if (EncounterDataManager.metConditions((Player) e.player, e, pl.region.forageData)) {
                if (Math.floor(Math.random() * 100) < pl.region.forageData.chance) {
                    ForageData.ForageItems i = pl.region.forageData.getForageItem();
                    e.setForagedItem(i.item);
                    char c = Character.toLowerCase(i.item.getDisplayName().charAt(0));
                    if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                        ((Player) e.player).sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Main.prefix + "Found an " + i.item.getDisplayName()));
                    } else {
                        ((Player) e.player).sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Main.prefix + "Found a " + i.item.getDisplayName()));
                    }
                } else {
                    e.setForagedItem(new ItemStack(Item.getByNameOrId("minecraft:air"), 1));
                    ((Player) e.player).sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Main.prefix + "No items found."));
                }
            } else {
                e.setForagedItem(new ItemStack(Item.getByNameOrId("minecraft:air"), 1));
                ((Player) e.player).sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Main.prefix + "No items found."));
            }
        }
    }
}
