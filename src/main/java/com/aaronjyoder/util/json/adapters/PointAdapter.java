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
import java.awt.Point;
import java.lang.reflect.Type;

public final class PointAdapter implements JsonSerializer<Point>, JsonDeserializer<Point> {

  // Moshi

  @ToJson
  PointJson toJson(Point point) {
    return new PointJson(point.x, point.y);
  }

  @FromJson
  Point fromJson(PointJson pointJson) {
    return new Point(pointJson.x, pointJson.y);
  }

  // Gson

  @Override
  public JsonElement serialize(Point src, Type srcType, JsonSerializationContext context) {
    JsonObject point = new JsonObject();
    point.add("x", new JsonPrimitive(src.x));
    point.add("y", new JsonPrimitive(src.y));
    return point;
  }

  @Override
  public Point deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
    JsonObject point = json.getAsJsonObject();
    return new Point(point.get("x").getAsInt(), point.get("y").getAsInt());
  }

}

final class PointJson {

  final int x;
  final int y;

  public PointJson(int x, int y) {
    this.x = x;
    this.y = y;
  }

}
