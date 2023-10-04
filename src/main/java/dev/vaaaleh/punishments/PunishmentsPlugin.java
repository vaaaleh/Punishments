package dev.vaaaleh.punishments;

import dev.vaaaleh.punishments.commands.*;
import dev.vaaaleh.punishments.database.Mongo;
import dev.vaaaleh.punishments.listener.PlayerListener;
import dev.vaaaleh.punishments.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PunishmentsPlugin extends JavaPlugin {
    private static Mongo database;
    private static PunishmentsPlugin instance;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        instance = this;
        try {
            System.out.println("Connecting to the mongo database.");
            database = new Mongo(this);
        } catch (Exception e) {
            System.out.println("Error while connecting to the mongo database.");
            return;
        }

        getCommand("history").setExecutor(new HistoryCommand());
        getCommand("ban").setExecutor(new BanCommand());
        getCommand("mute").setExecutor(new MuteCommand());
        getCommand("unmute").setExecutor(new UnmuteCommand());
        getCommand("unban").setExecutor(new UnbanCommand());
        getCommand("kick").setExecutor(new KickCommand());
        getCommand("blacklist").setExecutor(new BlacklistCommand());
        getCommand("unblacklist").setExecutor(new UnblacklistCommand());
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        Profile.preload();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (Profile profiles : Profile.getProfiles().values()) {
            profiles.save();
        }
    }

    public static Mongo getDatabase() {
        return database;
    }

    public static PunishmentsPlugin getInstance() {
        return instance;
    }
}
