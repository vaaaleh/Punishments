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

public class UnmuteCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!commandSender.hasPermission("punishments.mute")) {
            commandSender.sendMessage(CC.translate("&cNo permission."));
            return true;
        }

        if (args.length < 2) {
            commandSender.sendMessage(CC.translate("&cWrong Usage: /unmute <player> <reason> [-s]"));
            return true;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]); // DUMB BUT NEEDED CUZ IT WAS ACTING GAY SMH
        Profile profile = Profile.getByUuid(offlinePlayer.getUniqueId());

        if (profile == null) {
            commandSender.sendMessage(CC.translate("&cCould not resolve player's information."));
            return true;
        }

        if (profile.getActivePunishmentByType(PunishmentType.MUTE) == null) {
            commandSender.sendMessage(CC.translate("&cThat player is not muted."));
            return true;
        }

        String reason = args[1];

        Punishment punishment = profile.getActivePunishmentByType(PunishmentType.MUTE);
        punishment.setRemovedAt(System.currentTimeMillis());
        punishment.setRemovedReason(reason);
        punishment.setRemoved(true);

        if (commandSender instanceof Player) {
            punishment.setRemovedBy(((Player) commandSender).getUniqueId());
        }

        profile.save();

        if (offlinePlayer.isOnline()) profile.getPlayer().sendMessage(CC.translate("&aYou have been unmuted."));
        commandSender.sendMessage(CC.translate("&aYou have " + punishment.getType().getUndoContext() + ": &f" + profile.getUsername()));

        return false;
    }
}
