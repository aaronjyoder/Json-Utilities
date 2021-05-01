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
import java.awt.Color;
import java.lang.reflect.Type;

public class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {

  // Moshi

  @ToJson
  ColorJson toJson(Color color) {
    return new ColorJson(color.getRGB());
  }

  @FromJson
  Color fromJson(ColorJson colorJson) {
    return new Color(colorJson.rgb);
  }

  // Gson

  @Override
  public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject color = new JsonObject();
    color.add("rgb", new JsonPrimitive(src.getRGB()));
    return color;
  }

  @Override
  public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    JsonObject color = json.getAsJsonObject();
    return new Color(color.get("rgb").getAsInt());
  }

}

final class ColorJson {

  final int rgb;

  public ColorJson(int rgb) {
    this.rgb = rgb;
  }

}
