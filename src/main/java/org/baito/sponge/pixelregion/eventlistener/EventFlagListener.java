package org.baito.sponge.pixelregion.eventlistener;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;

public class EventFlagListener {

    @Listener
    public void onInteract(InteractBlockEvent.Secondary.OffHand e, @First Player p) {

    }

}
