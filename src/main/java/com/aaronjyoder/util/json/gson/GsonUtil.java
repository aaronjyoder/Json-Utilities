package com.aaronjyoder.util.json.gson;

import com.aaronjyoder.util.json.adapters.ColorAdapter;
import com.aaronjyoder.util.json.adapters.InstantAdapter;
import com.aaronjyoder.util.json.adapters.PointAdapter;
import com.aaronjyoder.util.json.adapters.RuntimeTypeAdapterFactory;
import com.aaronjyoder.util.json.adapters.UUIDAdapter;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.awt.Color;
import java.awt.Point;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class GsonUtil {

  private GsonUtil() {
  }

  private static final GsonBuilder jsonAdapterBuilder = new GsonBuilder()
      .registerTypeAdapter(Instant.class, new InstantAdapter())
      .registerTypeAdapter(UUID.class, new UUIDAdapter())
      .registerTypeAdapter(Point.class, new PointAdapter())
      .registerTypeAdapter(Color.class, new ColorAdapter())
      .setPrettyPrinting();

  public static void register(RuntimeTypeAdapterFactory<?>... factories) {
    for (RuntimeTypeAdapterFactory<?> factory : factories) {
      jsonAdapterBuilder.registerTypeAdapterFactory(factory);
    }
  }

  // Read

  @Nullable
  public static <T> T read(@Nonnull Path path, @Nonnull Class<T> type) throws IOException {
    if (Files.isRegularFile(path) && Files.isReadable(path)) {
      JsonReader jReader = new JsonReader(Files.newBufferedReader(path, StandardCharsets.UTF_8));
      return jsonAdapterBuilder.create().fromJson(jReader, type);
    }
    return null;
  }

  @Nullable
  public static <T> T read(@Nonnull Path path, @Nonnull Type type) throws IOException {
    if (Files.isRegularFile(path) && Files.isReadable(path)) {
      JsonReader jReader = new JsonReader(Files.newBufferedReader(path, StandardCharsets.UTF_8));
      return jsonAdapterBuilder.create().fromJson(jReader, type);
    }
    return null;
  }

  @Deprecated
  public static <T> T read(String file, Class<T> type) {
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

  @Deprecated
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

  public static <T> void write(@Nonnull Path path, @Nonnull Class<T> type, @Nonnull T object) throws IOException {
    Files.createDirectories(path.getParent());
    Files.writeString(path, jsonAdapterBuilder.create().toJson(object, type), StandardCharsets.UTF_8);
  }

  public static <T> void write(@Nonnull Path path, @Nonnull Type type, @Nonnull T object) throws IOException {
    Files.createDirectories(path.getParent());
    Files.writeString(path, jsonAdapterBuilder.create().toJson(object, type), StandardCharsets.UTF_8);
  }

  @Deprecated
  public static <T> void write(String file, Class<T> type, T object) {
    try {
      Writer writer = new FileWriter(file, StandardCharsets.UTF_8);
      writer.write(jsonAdapterBuilder.create().toJson(object, type));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Deprecated
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

  @Deprecated
  public static <T> void write(String directory, String fileName, Class<T> type, T object) {
    try {
      Files.createDirectories(Paths.get(directory));
      Writer writer = new FileWriter(directory + fileName, StandardCharsets.UTF_8);
      writer.write(jsonAdapterBuilder.create().toJson(object, type));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Deprecated
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
