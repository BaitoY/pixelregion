package org.baito.sponge.pixelregion.eventlistener;

import org.baito.sponge.pixelregion.eventflags.EventFlag;
import org.baito.sponge.pixelregion.eventflags.EventFlagManager;
import org.baito.sponge.pixelregion.eventflags.TriggerEnum;
import org.baito.sponge.pixelregion.playerdata.PlayerLink;
import org.baito.sponge.pixelregion.playerdata.PlayerLinkManager;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.text.Text;

public class EventFlagListener {

    @Listener
    public void onInteractBlock(InteractBlockEvent.Secondary.MainHand e, @First Player p) {
        PlayerLink pl = PlayerLinkManager.getLink(p);
        if (pl.inRegion && pl.region.eventFlags != null) {
            for (String ef : pl.region.eventFlags) {
                EventFlag event = EventFlagManager.getFlag(ef);
                if (event.trigger.mode == TriggerEnum.INTERACTBLOCK && EventFlagManager.metConditions(p, event.condition)
                && EventFlagManager.metTrigger(event.trigger, e)) {
                    p.sendMessage(Text.of("Event " + event.name + " fired!"));
                    EventFlagManager.runEffects(p, event.effects);
                }
            }
        }
    }

    @Listener
    public void onInteractItem(InteractItemEvent.Secondary.MainHand e, @First Player p) {
        PlayerLink pl = PlayerLinkManager.getLink(p);
        if (pl.inRegion && pl.region.eventFlags != null) {
            for (String ef : pl.region.eventFlags) {
                EventFlag event = EventFlagManager.getFlag(ef);
                if (event.trigger.mode == TriggerEnum.ITEM && EventFlagManager.metConditions(p, event.condition)
                        && EventFlagManager.metTrigger(event.trigger, e)) {
                    if (event.trigger.consumeOnUse) {
                        p.getItemInHand(HandTypes.MAIN_HAND).get().setQuantity(p.getItemInHand(HandTypes.MAIN_HAND).get().getQuantity()-1);
                    }
                    p.sendMessage(Text.of("Event " + event.name + " fired!"));
                    EventFlagManager.runEffects(p, event.effects);
                }
            }
        }
    }

}
