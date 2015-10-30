package fi.thl.termed.util;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

import java.io.IOException;

/**
 * Gson TypeAdapterFactory for unwrapping Hibernate proxied objects for serialization.
 */
public class HibernateProxyTypeAdapterFactory implements TypeAdapterFactory {

  @SuppressWarnings("unchecked")
  @Override
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
    return HibernateProxy.class.isAssignableFrom(type.getRawType())
           ? new HibernateProxyTypeAdapter<T>(gson) : null;
  }

  private final class HibernateProxyTypeAdapter<T> extends TypeAdapter<T> {

    private Gson gson;

    private HibernateProxyTypeAdapter(Gson gson) {
      this.gson = gson;
    }

    @Override
    public T read(JsonReader in) throws IOException {
      throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(JsonWriter out, T object) throws IOException {
      if (object == null) {
        out.nullValue();
        return;
      }

      TypeAdapter delegate =
          gson.getAdapter(TypeToken.get(Hibernate.getClass(object)));
      Object unproxiedValue =
          ((HibernateProxy) object).getHibernateLazyInitializer()
              .getImplementation();
      delegate.write(out, unproxiedValue);
    }
  }

}
