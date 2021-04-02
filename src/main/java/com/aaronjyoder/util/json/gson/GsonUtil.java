package com.aaronjyoder.util.json.gson;

import com.aaronjyoder.util.json.adapters.InstantAdapter;
import com.aaronjyoder.util.json.adapters.PointAdapter;
import com.aaronjyoder.util.json.adapters.RuntimeTypeAdapterFactory;
import com.aaronjyoder.util.json.adapters.UUIDAdapter;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.awt.Point;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;

public final class GsonUtil {

  private GsonUtil() {
  }

  private static final GsonBuilder jsonAdapterBuilder = new GsonBuilder()
      .registerTypeAdapter(Instant.class, new InstantAdapter())
      .registerTypeAdapter(UUID.class, new UUIDAdapter())
      .registerTypeAdapter(Point.class, new PointAdapter())
      .setPrettyPrinting();

  public static void register(RuntimeTypeAdapterFactory<?>... factories) {
    for (RuntimeTypeAdapterFactory<?> factory : factories) {
      jsonAdapterBuilder.registerTypeAdapterFactory(factory);
    }
  }

  // Read

  public static <T> T read(String file, Class<T> clazz) {
    File fileToRead = new File(file);
    if (fileToRead.exists()) {
      try (JsonReader jReader = new JsonReader(new FileReader(file, StandardCharsets.UTF_8))) {
        return jsonAdapterBuilder.create().fromJson(jReader, clazz);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static <T> T read(String file, Type type) {
    File fileToRead = new File(file);
    if (fileToRead.exists()) {
      try (JsonReader jReader = new JsonReader(new FileReader(file, StandardCharsets.UTF_8))) {
        return jsonAdapterBuilder.create().fromJson(jReader, type);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  // Write

  public static <T> void write(String file, Class<T> clazz, T object) {
    try {
      Writer writer = new FileWriter(file, StandardCharsets.UTF_8);
      writer.write(jsonAdapterBuilder.create().toJson(object, clazz));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static <T> void write(String file, Type type, T object) {
    try {
      Writer writer = new FileWriter(file, StandardCharsets.UTF_8);
      writer.write(jsonAdapterBuilder.create().toJson(object, type));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // Write with basic directory creation

  public static <T> void write(String directory, String fileName, Class<T> clazz, T object) {
    try {
      Files.createDirectories(Paths.get(directory));
      Writer writer = new FileWriter(directory + fileName, StandardCharsets.UTF_8);
      writer.write(jsonAdapterBuilder.create().toJson(object, clazz));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static <T> void write(String directory, String fileName, Type type, T object) {
    try {
      Files.createDirectories(Paths.get(directory));
      Writer writer = new FileWriter(directory + fileName, StandardCharsets.UTF_8);
      writer.write(jsonAdapterBuilder.create().toJson(object, type));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
