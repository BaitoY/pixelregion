package org.baito.sponge.pixelregion.commands;

import org.baito.sponge.pixelregion.encounterdata.EncounterDataManager;
import org.baito.sponge.pixelregion.eventflags.EventFlagManager;
import org.baito.sponge.pixelregion.regions.Region;
import org.baito.sponge.pixelregion.regions.RegionManager;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class EditCommand implements CommandExecutor {
    private String prefix = "&7[&dPXR Editor&7] &6";

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        String[] arg;
        String mode;
        if (args.getOne(Text.of("args")).isPresent()) {
            mode = ((String)args.getOne(Text.of("args")).get()).split(" ")[0];
            arg = new String[((String)args.getOne(Text.of("args")).get()).split(" ").length-1];
            for (int i = 1; i < ((String)args.getOne(Text.of("args")).get()).split(" ").length; i++) {
                arg[i-1] = ((String)args.getOne(Text.of("args")).get()).split(" ")[i];
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (String i : RegionManager.allRegions.keySet()) {
                sb.append("&a" + i + " ");
            }
            src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Loaded Regions: " + sb.toString()));
            sb = new StringBuilder();
            for (String i : EventFlagManager.events.keySet()) {
                sb.append("&a" + i + " ");
            }
            src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Loaded Events: " + sb.toString()));
            sb = new StringBuilder();
            for (String i : EncounterDataManager.encounterData.keySet()) {
                sb.append("&a" + i + " ");
            }
            src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Loaded Encounters: " + sb.toString()));
            sb = new StringBuilder();
            for (String i : EncounterDataManager.externalEncounterData.keySet()) {
                sb.append("&a" + i + " ");
            }
            src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Loaded External Encounters: " + sb.toString()));
            sb = new StringBuilder();
            for (String i : EncounterDataManager.forageData.keySet()) {
                sb.append("&a" + i + " ");
            }
            src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Loaded Forage: " + sb.toString()));
            return CommandResult.success();
        }
        switch (mode) {
            case "region":
                editRegion(src, arg);
                break;
            default:
                src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Unknown subcommand: " + arg[0]));
                break;
        }
        return CommandResult.success();
    }

    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
                .arguments(
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("args")))
                )
                .build();
    }

    private void editRegion(CommandSource src, String... arg) {
        if (arg.length < 1) {
            src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Please specify a region."));
            return;
        }
        if (!RegionManager.allRegions.containsKey(arg[0])) {
            src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Region " + arg[0] + " does not exist!"));
            return;
        }
        Region r = RegionManager.allRegions.get(arg[0]);
        // Display region info
        if (arg.length < 2) {
            StringBuilder sb = new StringBuilder();
            sb.append(prefix + r.name + " - World: " + r.world);
            if (r.displayName != null) {
                sb.append("\n&6Display Name: &r" + r.displayName.toPlain());
            }
            sb.append("\n&6Entry Notification: &r" + r.notifyEnter + "&6 Exit Notification: &r" + r.notifyExit);
            sb.append("\n&6Points: &r");
            for (int i = 0; i < r.points.length; i++) {
                sb.append("[" + r.points[i][0] + ", " + r.points[i][1] + "]");
                if (i != r.points.length - 1) {
                    sb.append(", ");
                }
            }
            if (r.yDim != null) {
                sb.append("\n&6Y-Dim: &r" + r.yDim[0] + " to " + r.yDim[1]);
            }
            sb.append("\n&6Weighting: &r" + r.weight);
            if (r.desc != null) {
                sb.append("\n&6Description: " + r.desc.toPlain());
            }
            if (r.encounterData != null) {
                sb.append("\n&6Encounters: &r");
                for (int i = 0; i < r.encounterData.length; i++) {
                    sb.append(r.encounterData[i]);
                    if (i != r.encounterData.length - 1) {
                        sb.append(", ");
                    }
                }
            }
            if (r.headbuttData != null) {
                sb.append("\n&6Headbutt data: &r" + r.headbuttData);
            }
            if (r.sweetScentData != null) {
                sb.append("\n&6Sweet Scent Data: &r" + r.sweetScentData);
            }
            if (r.forageData != null) {
                sb.append("\n&6Forage Data: &r" + r.forageData);
            }
            if (r.eventFlags != null) {
                sb.append("\n&6Event flags: &r");
                for (int i = 0; i < r.eventFlags.length; i++) {
                    sb.append(r.eventFlags[i]);
                    if (i != r.points.length - 1) {
                        sb.append(", ");
                    }
                }
            }
            src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(sb.toString()));
            return;
        }
        switch (arg[1]) {

        }
    }
}
