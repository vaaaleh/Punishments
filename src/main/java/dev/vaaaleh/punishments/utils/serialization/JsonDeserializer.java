package dev.vaaaleh.punishments.utils.serialization;

import com.google.gson.JsonObject;

public interface JsonDeserializer<T> {

	T deserialize(JsonObject object);

}
