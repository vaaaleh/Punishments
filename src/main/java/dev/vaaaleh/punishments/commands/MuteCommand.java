package dev.vaaaleh.punishments.commands;

import dev.vaaaleh.punishments.profile.Profile;
import dev.vaaaleh.punishments.profile.punishment.Punishment;
import dev.vaaaleh.punishments.profile.punishment.PunishmentType;
import dev.vaaaleh.punishments.utils.CC;
import dev.vaaaleh.punishments.utils.time.Duration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MuteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!commandSender.hasPermission("punishments.mute")) {
            commandSender.sendMessage(CC.translate("&cNo permission."));
            return true;
        }

        if (args.length < 3) {
            commandSender.sendMessage(CC.translate("&cWrong Usage: /mute <player> <duration> <reason> [-s]"));
            return true;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]); // DUMB BUT NEEDED CUZ IT WAS ACTING GAY SMH
        Profile profile = Profile.getByUuid(offlinePlayer.getUniqueId());
        if (profile == null) {
            commandSender.sendMessage(CC.translate("&cCould not resolve player's information."));
            return true;
        }

        Duration duration = Duration.fromString(args[1]);
        if (duration.getValue() == -1) {
            commandSender.sendMessage(CC.RED + "That duration is not valid.");
            commandSender.sendMessage(CC.RED + "Example: [perm/1y1m1w1d]");
            return true;
        }

        String reason = args[2];

        Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.MUTE, System.currentTimeMillis(),
                reason, duration.getValue());

        if (commandSender instanceof Player) {
            punishment.setAddedBy(((Player) commandSender).getUniqueId());
        }

        profile.getPunishments().add(punishment);
        profile.save();

        Player player = profile.getPlayer();

        boolean silent = false;

        for (String string : args) {
            if (string.contains("-s")) {
                silent = true;
            }
        }


        punishment.broadcast(commandSender.getName(), args[0], silent);
        commandSender.sendMessage(CC.translate("&aYou have " + punishment.getType().getContext() + ": &f" + profile.getUsername()));



        if (player != null) {
            String senderName = commandSender instanceof Player ? commandSender.getName() : CC.DARK_RED + "Console";
            player.sendMessage(CC.RED + "You have been " + punishment.getContext() + " by " +
                    senderName + CC.RED + ".");
            player.sendMessage(CC.RED + "The reason for this punishment: " + CC.WHITE +
                    punishment.getAddedReason());

            if (!punishment.isPermanent()) {
                player.sendMessage(CC.RED + "This mute will expire in " + CC.WHITE +
                        punishment.getTimeRemaining() + CC.RED + ".");
            }
        }

        return false;
    }
}
