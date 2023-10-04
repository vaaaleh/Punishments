package dev.vaaaleh.punishments.utils.serialization;

import com.google.gson.JsonObject;

public interface JsonSerializer<T> {

	JsonObject serialize(T t);

}
