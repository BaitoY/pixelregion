package org.baito.sponge.pixelregion;

import com.google.inject.Inject;
import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.battles.BattleQuery;
import com.pixelmonmod.pixelmon.battles.BattleRegistry;
import com.pixelmonmod.pixelmon.battles.controller.BattleControllerBase;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.WildPixelmonParticipant;
import com.pixelmonmod.pixelmon.battles.rules.BattleRules;
import com.pixelmonmod.pixelmon.battles.rules.clauses.BattleClause;
import com.pixelmonmod.pixelmon.entities.pixelmon.EntityPixelmon;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.battle.EnumBattleType;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import org.baito.sponge.pixelregion.encounterdata.EncounterData;
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
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.List;

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
            Sponge.getEventManager().registerListeners(this, new LoginMoveListener());
            logger.info("Pixelregion has been successfully enabled!");
        }
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

                    if (!checkPerm((Player)src, "pixelregion.cmd")) {
                        return CommandResult.success();
                    }
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
                                if (!checkPerm((Player)src, "pixelregion.cmd.reload")) {
                                    return CommandResult.success();
                                }
                                Config.load();
                                src.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(prefix + "Reloaded regions and encounters!"));
                                break;
                            case "info":
                                if (!checkPerm((Player)src, "pixelregion.cmd.info")) {
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
                                if (!checkPerm((Player)src, "pixelregion.cmd.togglenotif")) {
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
}
