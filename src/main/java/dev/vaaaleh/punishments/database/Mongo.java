package dev.vaaaleh.punishments.database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dev.vaaaleh.punishments.PunishmentsPlugin;

public class Mongo {


    private MongoDatabase mongoDatabase;
    private MongoCollection collection;

    public Mongo(PunishmentsPlugin plugin) {
        mongoDatabase = new MongoClient(plugin.getConfig().getString("MONGO.HOST"),
                plugin.getConfig().getInt("MONGO.PORT")).getDatabase(plugin.getConfig().getString("MONGO.DATABASE_NAME"));
        collection = mongoDatabase.getCollection("playerData");
    }

    public MongoCollection getCollection() {
        return collection;
    }
}
