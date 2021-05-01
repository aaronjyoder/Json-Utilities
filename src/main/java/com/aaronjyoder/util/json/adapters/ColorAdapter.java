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
import java.awt.Color;
import java.lang.reflect.Type;

public class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {

  // Moshi

  @ToJson
  Integer toJson(Color rgb) {
    return rgb.getRGB();
  }

  @FromJson
  Color fromJson(Integer rgb) {
    return new Color(rgb);
  }

  // Gson

  @Override
  public JsonElement serialize(Color src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src.getRGB());
  }

  @Override
  public Color deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    return new Color(json.getAsInt());
  }

}
