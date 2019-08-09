package org.baito.sponge.pixelregion.commands;

import org.baito.sponge.pixelregion.Config;
import org.baito.sponge.pixelregion.Main;
import org.baito.sponge.pixelregion.playerdata.PlayerLinkManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.serializer.TextSerializers;

public class BaseCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        /*if (RegionManager.allRegions.length < 1) {
            src.sendMessage(Text.of("There are no regions currently loaded. " +
                    "Try reloading the plugin or creating some in the config file."));
            return CommandResult.success();
        }
        for (int i = 0; i < RegionManager.allRegions.length; i++) {
            src.sendMessage(Text.of("Region " + RegionManager.allRegions[i].name));
            src.sendMessage(Text.of("Display Name: " + RegionManager.allRegions[i].displayName));
            src.sendMessage(Text.of("Notify Entry: " + RegionManager.allRegions[i].notifyEnter));
            src.sendMessage(Text.of("Notify Exit: " + RegionManager.allRegions[i].notifyExit));
            src.sendMessage(Text.of("Clear on Exit: " + RegionManager.allRegions[i].clearLeave));
            src.sendMessage(Text.of("Weighting: " + RegionManager.allRegions[i].weight));
            src.sendMessage(Text.of("Points"));
            Region r = RegionManager.allRegions[i];
            for (int x = 0; x < r.points.length; x++) {
                src.sendMessage(Text.of("X: " + r.points[x][0] + " Z: " + r.points[x][1]));
            }
            src.sendMessage(Text.of("\n"));
        }*/
        Config.load();
        PlayerLinkManager.setup();
        src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(Main.prefix + "Reload successful"));
        return CommandResult.success();
    }
}
