package com.aaronjyoder.util.json.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import java.lang.reflect.Type;
import java.util.UUID;

public final class UUIDAdapter implements JsonSerializer<UUID>, JsonDeserializer<UUID> {

  // Moshi

  @ToJson
  String toJson(UUID uuid) {
    return uuid.toString();
  }

  @FromJson
  UUID fromJson(String uuid) {
    return UUID.fromString(uuid);
  }

  // Gson

  @Override
  public JsonElement serialize(UUID src, Type srcType, JsonSerializationContext context) {
    return new JsonPrimitive(src.toString());
  }

  @Override
  public UUID deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
    return UUID.fromString(json.getAsString());
  }

}
