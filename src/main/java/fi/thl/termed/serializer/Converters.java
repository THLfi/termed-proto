package fi.thl.termed.serializer;

import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import fi.thl.termed.domain.Concept;
import fi.thl.termed.domain.PropertyValue;
import fi.thl.termed.domain.SchemeResource;
import fi.thl.termed.domain.SerializedConcept;
import fi.thl.termed.domain.SerializedConceptNarrowerTree;

public final class Converters {

  private Converters() {
  }

  /**
   * Register converter that is applied before serialization and again inverse applied after
   * deserialization
   */
  public static <A, B> GsonBuilder registerConverter(GsonBuilder builder,
                                                     Type originalType,
                                                     Type serializedType,
                                                     Converter<A, B> converter) {
    Preconditions.checkArgument(!originalType.equals(serializedType),
                                "Can't convert to same type.");
    return builder.registerTypeAdapter(originalType,
                                       new ConvertingSerializer<A, B>(serializedType, converter));
  }

  /**
   * Create and register converter for serializing java Date as ISO-date
   */
  public static GsonBuilder registerDateConverter(GsonBuilder builder) {
    return registerConverter(builder, Date.class, String.class, new DateConverter());
  }

  /**
   * Create and register converter that serializes property list as map
   */
  public static GsonBuilder registerPropertyListConverter(GsonBuilder builder) {
    Type propertyListType = new TypeToken<List<PropertyValue>>() {
    }.getType();
    Type propertyMapType = new TypeToken<Map<String, Map<String, List<String>>>>() {
    }.getType();

    return registerConverter(builder,
                             propertyListType,
                             propertyMapType,
                             new PropertyListConverter());
  }

  /**
   * Create and register Concept converter that truncates referenced and referring concepts
   */
  public static GsonBuilder registerConceptConverter(GsonBuilder builder, EntityManager em) {
    return registerConverter(builder,
                             Concept.class,
                             SerializedConcept.class,
                             new ConceptLoadingConverter(em));
  }

  /**
   * Create and register Concept converter that truncates serialized concepts
   */
  public static GsonBuilder registerTruncatingConceptConverter(GsonBuilder builder) {
    return registerConverter(builder,
                             Concept.class,
                             SchemeResource.class,
                             new ConceptTruncatingConverter());
  }

  /**
   * Create and register Concept converter that serializes narrower tree
   */
  public static GsonBuilder registerConceptNarrowerTreeConverter(GsonBuilder builder) {
    return registerConverter(builder,
                             Concept.class,
                             SerializedConceptNarrowerTree.class,
                             new ConceptNarrowerTreeConverter());
  }

}
