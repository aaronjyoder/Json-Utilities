package com.aaronjyoder.util.json.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import java.lang.reflect.Type;
import java.time.Instant;

public final class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

  // Moshi

  @ToJson
  InstantJson toJson(Instant instant) {
    return new InstantJson(instant.getEpochSecond(), instant.getNano());
  }

  @FromJson
  Instant fromJson(InstantJson instantJson) {
    return Instant.ofEpochSecond(instantJson.seconds, instantJson.nanos);
  }

  // Gson

  @Override
  public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context) {
    JsonObject instant = new JsonObject();
    instant.add("seconds", new JsonPrimitive(src.getEpochSecond()));
    instant.add("nanos", new JsonPrimitive(src.getNano()));
    return instant;
  }

  @Override
  public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
    JsonObject instant = json.getAsJsonObject();
    return Instant.ofEpochSecond(instant.get("seconds").getAsLong(), instant.get("nanos").getAsLong());
  }

}

final class InstantJson {

  final long seconds;
  final long nanos;

  public InstantJson(long seconds, long nanos) {
    this.seconds = seconds;
    this.nanos = nanos;
  }

}