package fi.thl.termed.serializer;

import com.google.common.base.Converter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Gson {@code JsonSerializer} and {@code JsonDeserializer} that converts value before serialization
 * and reverses the conversion after deserialization. Conversion is done using provided {@code
 * Converter}.
 */
public class ConvertingSerializer<A, B> implements JsonSerializer<A>, JsonDeserializer<A> {

  /**
   * Creates new {@code ConvertingSerializer}
   */
  public static <A, B> ConvertingSerializer<A, B> create(Type serializedType,
                                                         Converter<A, B> converter) {
    return new ConvertingSerializer<A, B>(serializedType, converter);
  }

  private Type serializedType;
  private Converter<A, B> converter;

  public ConvertingSerializer(Type serializedType, Converter<A, B> converter) {
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
