package com.aaronjyoder.util.json.moshi;

import com.aaronjyoder.util.json.adapters.InstantAdapter;
import com.aaronjyoder.util.json.adapters.PointAdapter;
import com.aaronjyoder.util.json.adapters.UUIDAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class MoshiUtil {

  private MoshiUtil() {
  }

  private static final Moshi.Builder jsonAdapterBuilder = new Moshi.Builder()
      .add(new InstantAdapter())
      .add(new UUIDAdapter())
      .add(new PointAdapter());

  public static void register(MoshiRuntimeTypeJsonAdapterFactory... factories) {
    for (MoshiRuntimeTypeJsonAdapterFactory factory : factories) {
      jsonAdapterBuilder.add(factory);
    }
  }

  private static Moshi jsonAdapter(MoshiRuntimeTypeJsonAdapterFactory... factories) {
    Moshi.Builder builder = new Moshi.Builder()
        .add(new InstantAdapter())
        .add(new UUIDAdapter())
        .add(new PointAdapter());

    for (MoshiRuntimeTypeJsonAdapterFactory factory : factories) {
      builder.add(factory);
    }

    return builder.build();
  }

  private static String fileToString(File file) throws IOException {
    if (file.exists()) {
      return Files.readString(file.toPath());
    }
    return "";
  }

  // Read

  public static <T> T read(String file, Class<T> clazz) {
    File fileToRead = new File(file);
    if (fileToRead.exists()) {
      try {
        JsonAdapter<T> jsonAdapter = jsonAdapter().adapter(clazz);
        return jsonAdapter.fromJson(fileToString(new File(file)));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public static <T> T read(String file, Type type) {
    File fileToRead = new File(file);
    if (fileToRead.exists()) {
      try {
        JsonAdapter<T> jsonAdapter = jsonAdapter().adapter(type);
        return jsonAdapter.fromJson(fileToString(new File(file)));
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
      writer.write(jsonAdapter().adapter(clazz).indent("  ").toJson(object));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static <T> void write(String file, Type type, T object) {
    try {
      Writer writer = new FileWriter(file, StandardCharsets.UTF_8);
      writer.write(jsonAdapter().adapter(type).indent("  ").toJson(object));
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
      writer.write(jsonAdapter().adapter(clazz).indent("  ").toJson(object));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static <T> void write(String directory, String fileName, Type type, T object) {
    try {
      Files.createDirectories(Paths.get(directory));
      Writer writer = new FileWriter(directory + fileName, StandardCharsets.UTF_8);
      writer.write(jsonAdapter().adapter(type).indent("  ").toJson(object));
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
