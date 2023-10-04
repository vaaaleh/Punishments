package dev.vaaaleh.punishments.listener;

import dev.vaaaleh.punishments.PunishmentsPlugin;
import dev.vaaaleh.punishments.profile.Profile;
import dev.vaaaleh.punishments.profile.punishment.Punishment;
import dev.vaaaleh.punishments.profile.punishment.PunishmentType;
import dev.vaaaleh.punishments.utils.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByUuid(player.getUniqueId());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (profile != null) {
                    profile.load();
                } else {
                    new Profile(player.getName(), player.getUniqueId());
                }
                cancel();
            }
        }.runTaskAsynchronously(PunishmentsPlugin.getInstance());

        if (profile.getActivePunishmentByType(PunishmentType.BAN) != null ) {
            Punishment punishment = profile.getActivePunishmentByType(PunishmentType.BAN);

            profile.getPlayer().kickPlayer(punishment.getKickMessage());
        } else if(profile.getActivePunishmentByType(PunishmentType.BLACKLIST) != null) {
            Punishment punishment = profile.getActivePunishmentByType(PunishmentType.BLACKLIST);

            profile.getPlayer().kickPlayer(punishment.getKickMessage());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Profile profile = Profile.getByUuid(event.getPlayer().getUniqueId());

        if (profile.getActivePunishmentByType(PunishmentType.MUTE) != null) {
            Punishment punishment = profile.getActivePunishmentByType(PunishmentType.MUTE);
            if (!punishment.hasExpired()) {
                event.setCancelled(true);
                profile.getPlayer().sendMessage(CC.CHAT_BAR);
                profile.getPlayer().sendMessage(CC.translate("&4&lYou are currently muted."));
                profile.getPlayer().sendMessage(CC.translate("&cReason: &f" + punishment.getAddedReason()));
                profile.getPlayer().sendMessage(CC.translate("&cDuration: &f" + punishment.getTimeRemaining()));
                profile.getPlayer().sendMessage(CC.CHAT_BAR);

            }
        }



    }


}
