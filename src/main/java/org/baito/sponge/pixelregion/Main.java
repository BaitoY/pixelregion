package org.baito.sponge.pixelregion;

import com.google.inject.Inject;
import com.pixelmonmod.pixelmon.Pixelmon;
import org.baito.sponge.pixelregion.encounterdata.EncounterData;
import org.baito.sponge.pixelregion.encounterdata.EncounterDataManager;
import org.baito.sponge.pixelregion.eventflags.PlayerFlagDataManager;
import org.baito.sponge.pixelregion.eventlistener.EventFlagListener;
import org.baito.sponge.pixelregion.eventlistener.ExternalMoveListener;
import org.baito.sponge.pixelregion.eventlistener.LoginMoveListener;
import org.baito.sponge.pixelregion.playerdata.PlayerLinkManager;
import org.baito.sponge.pixelregion.regions.RegionManager;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.concurrent.TimeUnit;

@Plugin(
        id = "pixelregion",
        name = "Pixelregion",
        version = "1.0-SNAPSHOT"
)

public class Main {
    @Inject
    private Logger logger;
    public static String prefix = "&7[&dPXR&7] &6";

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        if (!Sponge.getPluginManager().getPlugin("pixelmon").isPresent()) {
            logger.info("Pixelmon is not installed! Pixelregion will not be enabled.");
            return;
        }
        Config.setup();
        Config.load();
        if (RegionManager.allRegions.isEmpty()) {
            logger.info("No regions have been detected, Pixelregion will not be enabled.");
        } else {
            registerCommands();
            Sponge.getEventManager().registerListeners(this, new LoginMoveListener());
            Sponge.getEventManager().registerListeners(this, new EventFlagListener());
            Pixelmon.EVENT_BUS.register(new ExternalMoveListener());
            Task.builder().interval(50, TimeUnit.MILLISECONDS).execute(() -> {
                LoginMoveListener.interval++;
                if (LoginMoveListener.interval > 20) {
                    LoginMoveListener.interval = 0;
                }
            }).submit(this);
            logger.info("Pixelregion has been successfully enabled!");
        }
    }

    @Listener
    public void onServerEnd(GameStoppingServerEvent e) {
        PlayerFlagDataManager.save();
    }

    private boolean checkPerm(Player pl, String p) {
        if (!pl.hasPermission(p)) {
            pl.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "You do not have permission to use this command."));
            return false;
        }
        return true;
    }

    private void registerCommands() {
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .description(Text.of("The base command for Pixelregion"))
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("sub")))
                )
                .executor((CommandSource src, CommandContext args) -> {

                    Player plr = src instanceof Player ? (Player) src : null;
                    if (!args.<String>getOne(Text.of("sub")).isPresent()) {
                        PaginationList.builder()
                                .title(TextSerializers.FORMATTING_CODE.deserialize("&dPixelregion"))
                                .padding(TextSerializers.FORMATTING_CODE.deserialize("&7="))
                                .contents(TextSerializers.FORMATTING_CODE.deserialize("&6Version: &b&o1.0-SNAPSHOT"),
                                        TextSerializers.FORMATTING_CODE.deserialize("&6Available Commands: &b&oreload, rinfo, togglenotif"))
                                .sendTo(src);
                    } else {
                        String sub = args.<String>getOne(Text.of("sub")).get();
                        switch (sub) {
                            case "reload":
                                if (!checkPerm(plr, "pixelregion.cmd.reload")) {
                                    return CommandResult.success();
                                }
                                Config.load();
                                src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Reloaded config!"));
                                break;
                            case "info":
                                if (!checkPerm(plr, "pixelregion.cmd.info")) {
                                    return CommandResult.success();
                                }
                                if (plr != null) {
                                    if (PlayerLinkManager.getLink(plr).region != null) {
                                        if (PlayerLinkManager.getLink(plr).region.desc != null || PlayerLinkManager.getLink(plr).region.encounterData != null) {
                                            if (PlayerLinkManager.getLink(plr).region.desc != null) {
                                                plr.sendMessage(
                                                        Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize(prefix)).
                                                                append(PlayerLinkManager.getLink(plr).region.desc).build());
                                            }
                                            if (PlayerLinkManager.getLink(plr).region.encounterData != null) {
                                                plr.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Encounter Data [BETA]"));
                                                int table = 1;
                                                for (String ed : PlayerLinkManager.getLink(plr).region.encounterData) {
                                                    EncounterData i = EncounterDataManager.getData(ed);
                                                    plr.sendMessage(
                                                            Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("\n&6> Table " + table + " <" + "\n" + i.info())).build());
                                                    table++;
                                                }
                                            }
                                        } else {
                                            plr.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "This region has no information."));
                                        }
                                    } else {
                                        plr.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "You are not in a region!"));
                                    }
                                } else {
                                    src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "You are not a player!"));
                                }
                                break;
                            case "togglenotif":
                                if (!checkPerm(plr, "pixelregion.cmd.togglenotif")) {
                                    return CommandResult.success();
                                }
                                if (plr != null) {
                                    PlayerLinkManager.getLink(plr).toggleNotif();
                                    if (PlayerLinkManager.getLink(plr).sendNotif) {
                                        plr.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Notifications are now enabled."));
                                    } else {
                                        plr.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Notifications are now disabled."));
                                    }
                                }
                                break;
                            default:
                                src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Unrecognised subcommand: \"" + sub + "\""));
                                break;
                        }
                    }
                    return CommandResult.success();
                }).build(), "pxr");

        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .description(Text.of("Region editor for Pixelregion"))
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("sub")))
                )
                .executor((CommandSource src, CommandContext args) -> {
                    return CommandResult.success();
                }).build(), "pxre");
    }

    public Logger getLogger() {
        return logger;
    }

}
