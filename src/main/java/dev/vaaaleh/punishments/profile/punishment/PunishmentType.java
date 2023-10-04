package dev.vaaaleh.punishments.profile.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
@AllArgsConstructor
public enum PunishmentType {

	BLACKLIST("Blacklist", "blacklisted", "unblacklisted"),
	BAN("Ban", "banned", "unbanned"),
	MUTE("Mute", "muted", "unmuted"),
	KICK("Kick", "kicked", null);

	private String readable;
	private String context;
	private String undoContext;



}
