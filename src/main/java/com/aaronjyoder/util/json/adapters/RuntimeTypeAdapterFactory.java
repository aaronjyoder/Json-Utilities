package com.aaronjyoder.util.json.adapters;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nullable;

public class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory, JsonAdapter.Factory {

  private static final String DEFAULT_CLASS_NAME_PROPERTY = "type";

  private final Class<?> baseClass;
  private final String classNameProperty;
  private final Map<String, Class<?>> classNameToClass = new HashMap<>();
  private final Map<Class<?>, String> classToClassName = new HashMap<>();
  private final boolean maintainType;

  private RuntimeTypeAdapterFactory(final Class<?> baseClass, final String classNameProperty, boolean maintainType) {
    if (classNameProperty == null || baseClass == null) {
      throw new NullPointerException();
    }
    this.baseClass = baseClass;
    this.classNameProperty = classNameProperty;
    this.maintainType = maintainType;
  }

  /**
   * Creates a new runtime type adapter for {@code expectedClass} using {@code DEFAULT_CLASS_NAME_PROPERTY} as the type field name.
   */
  public static <T> RuntimeTypeAdapterFactory<T> of(final Class<T> expectedClass) {
    return new RuntimeTypeAdapterFactory<>(expectedClass, DEFAULT_CLASS_NAME_PROPERTY, false);
  }

  /**
   * Creates a new runtime type adapter using for {@code expectedClass} using {@code classNameProperty} as the type field name. Type field names are case sensitive.
   */
  public static <T> RuntimeTypeAdapterFactory<T> of(final Class<T> expectedClass, final String classNameProperty) {
    return new RuntimeTypeAdapterFactory<>(expectedClass, classNameProperty, false);
  }

  /**
   * Creates a new runtime type adapter using for {@code expectedClass} using {@code classNameProperty} as the type field name. Type field names are case sensitive. {@code
   * maintainType} flag decide if the type will be stored in pojo or not.
   */
  public static <T> RuntimeTypeAdapterFactory<T> of(final Class<T> expectedClass, final String classNameProperty, final boolean maintainType) {
    return new RuntimeTypeAdapterFactory<>(expectedClass, classNameProperty, maintainType);
  }

  /**
   * Registers {@code concreteClass} identified by {@code className}. Labels are case sensitive.
   *
   * @throws IllegalArgumentException if either {@code concreteClass} or {@code className} have already been registered on this type adapter.
   */
  public RuntimeTypeAdapterFactory<T> with(final Class<? extends T> concreteClass, final String className) throws IllegalArgumentException {
    if (classNameToClass.containsKey(className)) {
      throw new IllegalArgumentException(className + " is already registered for " + concreteClass);
    }
    if (classToClassName.containsKey(concreteClass)) {
      throw new IllegalArgumentException(concreteClass + " is already registered for " + className);
    }
    classNameToClass.put(className, concreteClass);
    classToClassName.put(concreteClass, className);
    return this;
  }

  /**
   * Registers {@code concreteClass} identified by its {@link Class#getSimpleName simple name}. Labels are case sensitive.
   *
   * @throws IllegalArgumentException if either {@code className} or its simple name have already been registered on this type adapter.
   */
  public RuntimeTypeAdapterFactory<T> with(final Class<? extends T> concreteClass) {
    return with(concreteClass, concreteClass.getSimpleName());
  }

  // Gson
  @Override
  public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
    if (type.getRawType() != baseClass) {
      return null;
    }

    final Map<String, TypeAdapter<?>> labelToDelegate
        = new LinkedHashMap<>();
    final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate
        = new LinkedHashMap<Class<?>, TypeAdapter<?>>();
    for (Map.Entry<String, Class<?>> entry : classNameToClass.entrySet()) {
      TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
      labelToDelegate.put(entry.getKey(), delegate);
      subtypeToDelegate.put(entry.getValue(), delegate);
    }

    return new TypeAdapter<R>() {
      @Override
      public R read(JsonReader in) throws IOException {
        JsonElement jsonElement = Streams.parse(in);
        JsonElement labelJsonElement;
        if (maintainType) {
          labelJsonElement = jsonElement.getAsJsonObject().get(classNameProperty);
        } else {
          labelJsonElement = jsonElement.getAsJsonObject().remove(classNameProperty);
        }

        if (labelJsonElement == null) {
          throw new JsonParseException("cannot deserialize " + baseClass
              + " because it does not define a field named " + classNameProperty);
        }
        String label = labelJsonElement.getAsString();
        @SuppressWarnings("unchecked") // registration requires that subtype extends T
        TypeAdapter<R> delegate = (TypeAdapter<R>) labelToDelegate.get(label);
        if (delegate == null) {
          throw new JsonParseException("cannot deserialize " + baseClass + " subtype named "
              + label + "; did you forget to register a subtype?");
        }
        return delegate.fromJsonTree(jsonElement);
      }

      @Override
      public void write(JsonWriter out, R value) throws IOException {
        Class<?> srcType = value.getClass();
        String label = classToClassName.get(srcType);
        @SuppressWarnings("unchecked") // registration requires that subtype extends T
        TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);
        if (delegate == null) {
          throw new JsonParseException("cannot serialize " + srcType.getName()
              + "; did you forget to register a subtype?");
        }
        JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();

        if (maintainType) {
          Streams.write(jsonObject, out);
          return;
        }

        JsonObject clone = new JsonObject();

        if (jsonObject.has(classNameProperty)) {
          throw new JsonParseException("cannot serialize " + srcType.getName()
              + " because it already defines a field named " + classNameProperty);
        }
        clone.add(classNameProperty, new JsonPrimitive(label));

        for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
          clone.add(e.getKey(), e.getValue());
        }
        Streams.write(clone, out);
      }
    }.nullSafe();
  }

  // Moshi
  @Nullable
  @Override
  public JsonAdapter<?> create(final Type type, final Set<? extends Annotation> annotations, final Moshi moshi) {
    if (!(type instanceof Class)) {
      return null;
    }
    final Class<?> typeAsClass = (Class<?>) type;
    if (!baseClass.isAssignableFrom(typeAsClass)) {
      return null;
    }
    final JsonAdapter<Object> jsonObjectJsonAdapter = moshi.nextAdapter(this, Map.class, ImmutableSet.of());
    final LoadingCache<Class<?>, JsonAdapter<Object>> jsonAdaptersCache = CacheBuilder.newBuilder()
        .build(new CacheLoader<>() {
          @Override
          public JsonAdapter<Object> load(final Class<?> clazz) {
            return moshi.nextAdapter(RuntimeTypeAdapterFactory.this, clazz, ImmutableSet.copyOf(clazz.getAnnotations()));
          }
        });
    return new JsonAdapter<>() {
      @Nullable
      @Override
      public Object fromJson(final com.squareup.moshi.JsonReader jsonReader)
          throws IOException {
        try {
          @SuppressWarnings("unchecked") final Map<String, Object> jsonObject = (Map<String, Object>) jsonReader.readJsonValue();
          assert jsonObject != null;
          final Object rawClassName = jsonObject.get(classNameProperty);
          if (!(rawClassName instanceof String)) {
            throw new IOException("Type name: expected a string in " + classNameProperty + ", but got " + rawClassName);
          }
          final String className = (String) rawClassName;
          final Class<?> concreteClass = classNameToClass.get(className);
          if (concreteClass == null) {
            throw new IOException("No mapping registered for " + className);
          }
          final JsonAdapter<Object> jsonAdapter = jsonAdaptersCache.get(concreteClass);
          return jsonAdapter.fromJsonValue(jsonObject);
        } catch (final ExecutionException ex) {
          throw new RuntimeException(ex);
        }
      }

      @Override
      public void toJson(final com.squareup.moshi.JsonWriter jsonWriter, @Nullable final Object value)
          throws IOException {
        try {
          assert value != null;
          final Class<?> concreteClass = value.getClass();
          final String className = classToClassName.get(concreteClass);
          if (className == null) {
            throw new IOException("No mapping registered for " + concreteClass);
          }
          final JsonAdapter<Object> valueJsonAdapter = jsonAdaptersCache.get(concreteClass);
          @SuppressWarnings("unchecked") final Map<String, Object> jsonObject = (Map<String, Object>) valueJsonAdapter.toJsonValue(value);
          assert jsonObject != null;
          jsonObject.put(classNameProperty, className);
          jsonObjectJsonAdapter.toJson(jsonWriter, jsonObject);
        } catch (final ExecutionException ex) {
          throw new RuntimeException(ex);
        }
      }
    };
  }

}
