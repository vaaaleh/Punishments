package dev.vaaaleh.punishments.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.vaaaleh.punishments.PunishmentsPlugin;
import dev.vaaaleh.punishments.profile.punishment.Punishment;
import dev.vaaaleh.punishments.profile.punishment.PunishmentType;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Profile {

    @Getter
    private static Map<UUID, Profile> profiles = new HashMap<>();
    private static MongoCollection<Document> collection = PunishmentsPlugin.getDatabase().getCollection();
    @Getter private final List<Punishment> punishments;
    @Getter private final UUID uuid;
    @Getter private String username;
    @Getter private boolean loaded=false;


    public Profile(String username, UUID uuid) {
        this.punishments = new ArrayList<>();
        this.username = username;
        this.uuid = uuid;


        load();
    }

    public static void preload() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = new Profile(player.getName(), player.getUniqueId());

            try {
                profile.load();
            } catch (Exception e) {
                player.kickPlayer(ChatColor.RED + "The server is loading...");
                continue;
            }

            profiles.put(player.getUniqueId(), profile);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Profile profile : Profile.getProfiles().values()) {
                    profile.save();
                }
            }
        }.runTaskTimerAsynchronously(PunishmentsPlugin.getInstance(), 36000L, 36000L);

    }



    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public Punishment getActivePunishmentByType(PunishmentType type) {
        for (Punishment punishment : punishments) {
            if (punishment.getType() == type && !punishment.isRemoved() && !punishment.hasExpired()) {
                return punishment;
            }
        }

        return null;
    }

    public void load() {
        Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document != null) {
            if (username == null) {
                username = document.getString("name");
            }
            JsonArray punishmentList = new JsonParser().parse(document.getString("punishments")).getAsJsonArray();

            for (JsonElement punishmentData : punishmentList) {
                Punishment punishment = Punishment.DESERIALIZER.deserialize(punishmentData.getAsJsonObject());

                if (punishment != null) {
                    this.punishments.add(punishment);

                }
            }
        }

        loaded = true;
    }

    public void save() {
        Document document = new Document();
        document.put("uuid", uuid.toString());
        document.put("name", username);


        JsonArray punishmentList = new JsonArray();

        for (Punishment punishment : this.punishments) {
            punishmentList.add(Punishment.SERIALIZER.serialize(punishment));
        }

        document.put("punishments", punishmentList.toString());

        collection.replaceOne(Filters.eq("uuid", uuid.toString()), document, new ReplaceOptions().upsert(true));
    }
    public static Profile getByName(String username) {
        Player player = Bukkit.getPlayer(username);

        if (player != null) {
            return profiles.get(player.getUniqueId());
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

        if (offlinePlayer.hasPlayedBefore()) {
            if (profiles.containsKey(offlinePlayer.getUniqueId())) {
                return profiles.get(offlinePlayer.getUniqueId());
            }

            return new Profile(offlinePlayer.getName(), offlinePlayer.getUniqueId());
        }

        return null;
    }

    public static Profile getByUuid(UUID uuid) {
        if (profiles.containsKey(uuid)) {
            return profiles.get(uuid);
        }

        return new Profile(null, uuid);
    }



}
