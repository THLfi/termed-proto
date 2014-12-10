package fi.thl.termed.util;

import com.google.common.base.Converter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Converts value before serialization. After deserialization reverse conversion.
 */
public class ConvertingSerializer<A, B> implements JsonSerializer<A>, JsonDeserializer<A> {

  private Class<B> serializedType;
  private Converter<A, B> converter;

  public ConvertingSerializer(Class<B> serializedType, Converter<A, B> converter) {
    this.serializedType = serializedType;
    this.converter = converter;
  }

  @Override
  public JsonElement serialize(A value, Type type, JsonSerializationContext serializationContext) {
    B serialized = converter.convert(value);
    return serializationContext.serialize(serialized, serializedType);
  }

  @Override
  public A deserialize(JsonElement jsonElement, Type type,
                       JsonDeserializationContext deserializationContext)
      throws JsonParseException {
    B serialized = deserializationContext.deserialize(jsonElement, serializedType);
    return converter.reverse().convert(serialized);
  }

}
