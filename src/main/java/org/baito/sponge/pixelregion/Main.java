package org.baito.sponge.pixelregion;

import com.google.inject.Inject;
import org.baito.sponge.pixelregion.encounterdata.EncounterData;
import org.baito.sponge.pixelregion.playerdata.PlayerLink;
import org.baito.sponge.pixelregion.playerdata.PlayerLinkManager;
import org.baito.sponge.pixelregion.regions.Region;
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
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

@Plugin(
        id = "pixelregion",
        name = "Pixelregion",
        version = "1.0-SNAPSHOT"
)
public class Main {
    @Inject
    private Logger logger;
    private boolean enabled;
    public static String prefix = "&7[&dPXR&7] &6";

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        if (!Sponge.getPluginManager().getPlugin("pixelmon").isPresent()) {
            enabled = false;
            logger.info("Pixelmon is not installed! Pixelregion will not be enabled.");
            return;
        }
        Config.setup();
        Config.load();
        if (RegionManager.allRegions.length < 1) {
            enabled = false;
            logger.info("No regions have been detected, Pixelregion will not be enabled.");
        } else {
            enabled = true;
            registerCommands();
            logger.info("Pixelregion has been successfully enabled!");
        }
    }

    @Listener
    public void onMove(MoveEntityEvent e) {
        if (enabled && e.getTargetEntity() instanceof Player) {
            manageRegion((Player) e.getTargetEntity());
        }
    }

    @Listener
    public void onLogin(ClientConnectionEvent.Join e) {
        if (enabled) {
            manageRegion(e.getTargetEntity());
        }
    }

    private void manageRegion(Player e) {
        String UUID = e.getUniqueId().toString();
        PlayerLink PL = PlayerLinkManager.getLink(e);
        Region R = RegionManager.getRegionInside(e);
        if (R != null) {
            if (!PL.inRegion || !R.name.equals(PL.region.name)) {
                PL.inRegion = true;
                PL.region = R;
                if (R.notifyEnter && PL.sendNotif) {
                    e.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Now entering >>> &f&l").toBuilder().append(R.displayName).build());
                }
            }
        } else if (PL.inRegion) {
            PL.inRegion = false;
            if (PL.region.notifyExit && PL.sendNotif) {
                e.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Now exiting >>> &f&l").toBuilder().append(PL.region.displayName).build());
            }
            PL.region = null;
        }
    }

    private void registerCommands() {
        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .description(Text.of("The base command for Pixelregion"))
                .arguments(
                        GenericArguments.optional(GenericArguments.string(Text.of("sub")))
                )
                .executor((CommandSource src, CommandContext args) -> {
                    if (!args.<String>getOne(Text.of("sub")).isPresent()) {
                        PaginationList.builder()
                                .title(TextSerializers.FORMATTING_CODE.deserialize("&dPixelregion"))
                                .padding(TextSerializers.FORMATTING_CODE.deserialize("&7="))
                                .contents(TextSerializers.FORMATTING_CODE.deserialize("&6Version: &b&o1.0-SNAPSHOT"),
                                        TextSerializers.FORMATTING_CODE.deserialize("&6Available Commands: &b&oreload, rinfo, togglenotif"))
                                .sendTo(src);
                    } else {
                        String sub = args.<String>getOne(Text.of("sub")).get();
                        Player plr = src instanceof Player ? (Player) src : null;
                        switch (sub) {
                            case "reload":
                                Config.load();
                                src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Reloaded regions!"));
                                break;
                            case "rinfo":
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
                                                for (EncounterData i : PlayerLinkManager.getLink(plr).region.encounterData) {
                                                    plr.sendMessage(
                                                            Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize(i.info())).build());
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
}
