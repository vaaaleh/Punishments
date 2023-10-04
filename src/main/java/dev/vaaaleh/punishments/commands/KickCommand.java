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
import java.util.UUID;

public class KickCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!commandSender.hasPermission("punishments.kick")) {
            commandSender.sendMessage(CC.translate("&cNo permission."));
            return true;
        }

        if (args.length < 2) {
            commandSender.sendMessage(CC.translate("&cWrong Usage: /kick <player> <reason> [-s]"));
            return true;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]); // DUMB BUT NEEDED CUZ IT WAS ACTING GAY SMH
        Profile profile = Profile.getByUuid(offlinePlayer.getUniqueId());

        if (profile == null) {
            commandSender.sendMessage(CC.translate("&cCould not resolve player's information."));
            return true;
        }

        if (!offlinePlayer.isOnline()) {
            commandSender.sendMessage(CC.translate("&cThat player is not online."));
            return true;
        }

        String reason = args[1];

        Punishment punishment = new Punishment(UUID.randomUUID(), PunishmentType.KICK, System.currentTimeMillis(),
                reason, -1);

        if (commandSender instanceof Player) {
            punishment.setAddedBy(((Player) commandSender).getUniqueId());
        }

        profile.getPunishments().add(punishment);
        profile.save();
        profile.getPlayer().kickPlayer(punishment.getKickMessage());

        boolean silent = false;

        for (String string : args) {
            if (string.contains("-s")) {
                silent = true;
            }
        }

        punishment.broadcast(commandSender.getName(), args[0], silent);
        commandSender.sendMessage(CC.translate("&aYou have " + punishment.getType().getContext() + ": &f" + profile.getUsername()));


        return false;
    }
}
