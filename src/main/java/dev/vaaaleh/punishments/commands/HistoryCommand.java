package dev.vaaaleh.punishments.commands;

import dev.vaaaleh.punishments.profile.Profile;
import dev.vaaaleh.punishments.profile.punishment.Punishment;
import dev.vaaaleh.punishments.profile.punishment.PunishmentType;
import dev.vaaaleh.punishments.utils.CC;
import dev.vaaaleh.punishments.utils.time.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class HistoryCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("punishments.history")) {
            commandSender.sendMessage(CC.translate("&cNo permission."));
            return true;
        }

        if (args.length < 1) {
            commandSender.sendMessage(CC.translate("&cWrong usage: /history <player>"));
            return true;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]); // DUMB BUT NEEDED CUZ IT WAS ACTING GAY SMH
        Profile profile = Profile.getByUuid(offlinePlayer.getUniqueId());

        commandSender.sendMessage(CC.CHAT_BAR);
        commandSender.sendMessage(CC.translate("&6Viewing " + args[0] + "'s punishment history. &e(" + profile.getPunishments().size()) + ")");
        for (Punishment punishment : profile.getPunishments()) {
            OfflinePlayer issuer = null;
            if (punishment.getAddedBy() != null) {
               issuer = Bukkit.getOfflinePlayer(punishment.getAddedBy());
            }
            String issuerName = issuer == null ? "CONSOLE" : issuer.getName();

            if (punishment.hasExpired() && !(punishment.getType() == PunishmentType.KICK) || punishment.isRemoved() && !(punishment.getType() == PunishmentType.KICK)) {
                commandSender.sendMessage(CC.translate("&c&m" + punishment.getType().getReadable() + " on " + TimeUtil.millisToDate(punishment.getAddedAt()) + " by " + issuerName + " for " + punishment.getAddedReason()));
            } else if (!punishment.hasExpired()){
                commandSender.sendMessage(CC.translate("&a" + punishment.getType().getReadable() + " on " + TimeUtil.millisToDate(punishment.getAddedAt()) + " by " + issuerName + " for " + punishment.getAddedReason()));
            } else {
                commandSender.sendMessage(CC.translate("&c" + punishment.getType().getReadable() + " on " + TimeUtil.millisToDate(punishment.getAddedAt()) + " by " + issuerName + " for " + punishment.getAddedReason()));
            }
        }
        commandSender.sendMessage(CC.translate("This is the end of " + args[0] + "'s punishment history."));
        commandSender.sendMessage(CC.CHAT_BAR);
        return false;
    }
}
