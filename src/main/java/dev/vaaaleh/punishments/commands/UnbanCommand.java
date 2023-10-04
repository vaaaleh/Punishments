package dev.vaaaleh.punishments.commands;

import dev.vaaaleh.punishments.profile.Profile;
import dev.vaaaleh.punishments.profile.punishment.Punishment;
import dev.vaaaleh.punishments.profile.punishment.PunishmentType;
import dev.vaaaleh.punishments.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnbanCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!commandSender.hasPermission("punishments.ban")) {
            commandSender.sendMessage(CC.translate("&cNo permission."));
            return true;
        }

        if (args.length < 2) {
            commandSender.sendMessage(CC.translate("&cWrong Usage: /unban <player> <reason> [-s]"));
            return true;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]); // DUMB BUT NEEDED CUZ IT WAS ACTING GAY SMH
        Profile profile = Profile.getByUuid(offlinePlayer.getUniqueId());

        if (profile == null) {
            commandSender.sendMessage(CC.translate("&cCould not resolve player's information."));
            return true;
        }

        if (profile.getActivePunishmentByType(PunishmentType.BAN) == null) {
            commandSender.sendMessage(CC.translate("&cThat player is not banned."));
            return true;
        }

        String reason = args[1];


        Punishment punishment = profile.getActivePunishmentByType(PunishmentType.BAN);
        punishment.setRemovedAt(System.currentTimeMillis());
        punishment.setRemovedReason(reason);
        punishment.setRemoved(true);

        if (commandSender instanceof Player) {
            punishment.setRemovedBy(((Player) commandSender).getUniqueId());
        }

        profile.save();

        boolean silent = false;

        for (String string : args) {
            if (string.contains("-s")) {
                silent = true;
            }
        }

        punishment.broadcastRemoved(commandSender.getName(), args[0], silent, punishment.getType());
        commandSender.sendMessage(CC.translate("&aYou have " + punishment.getType().getUndoContext() + ": &f" + profile.getUsername()));


        return false;
    }
}
