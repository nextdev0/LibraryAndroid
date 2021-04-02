package com.nextstory.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * {@link Gson} JSON 서비스
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class GsonJsonService implements JsonService {
    private final Gson gson;

    public GsonJsonService() {
        Map<Class<?>, Function<String, Number>> numberFunctions = new LinkedHashMap<>();
        numberFunctions.put(byte.class, Byte::parseByte);
        numberFunctions.put(Byte.class, Byte::parseByte);
        numberFunctions.put(int.class, Integer::parseInt);
        numberFunctions.put(Integer.class, Integer::parseInt);
        numberFunctions.put(short.class, Short::parseShort);
        numberFunctions.put(Short.class, Short::parseShort);
        numberFunctions.put(long.class, Long::parseLong);
        numberFunctions.put(Long.class, Long::parseLong);
        numberFunctions.put(float.class, Float::parseFloat);
        numberFunctions.put(Float.class, Float::parseFloat);
        numberFunctions.put(double.class, Double::parseDouble);
        numberFunctions.put(Double.class, Double::parseDouble);
        gson = new GsonBuilder()
                .registerTypeAdapterFactory(new TypeAdapterFactory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                        Class<?> rawType = type.getRawType();
                        if (numberFunctions.containsKey(rawType)) {
                            return (TypeAdapter<T>) new TypeAdapter<Number>() {
                                @Override
                                public void write(JsonWriter out, Number value) throws IOException {
                                    out.value(value);
                                }

                                @Override
                                public Number read(JsonReader in) {
                                    try {
                                        String value = in.nextString();
                                        return Objects.requireNonNull(numberFunctions.get(rawType))
                                                .apply(value);
                                    } catch (Throwable ignore) {
                                        return 0;
                                    }
                                }
                            };
                        }
                        return null;
                    }
                })
                .create();
    }

    @Override
    public String serialize(Object source) {
        return gson.toJson(source);
    }

    @Override
    public <T> T deserialize(String jsonSource, Class<T> typeClass) {
        return gson.fromJson(jsonSource, typeClass);
    }

    @Override
    public <T> T deserialize(String jsonSource, Type type) {
        return gson.fromJson(jsonSource, type);
    }
}
