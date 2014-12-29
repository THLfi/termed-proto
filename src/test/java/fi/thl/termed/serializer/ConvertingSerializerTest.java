package fi.thl.termed.serializer;

import com.google.common.base.Converter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConvertingSerializerTest {

  private class BooleanToIntegerConverter extends Converter<Boolean, Integer> {

    @Override
    protected Integer doForward(Boolean b) {
      return b ? 1 : 0;
    }

    @Override
    protected Boolean doBackward(Integer i) {
      return i.equals(1);
    }
  }

  private Gson gson;

  @Before
  public void setUp() {
    GsonBuilder builder = new GsonBuilder();
    ConvertingSerializer.registerConverter(builder, Boolean.class, Integer.class,
                                           new BooleanToIntegerConverter());
    this.gson = builder.create();
  }

  @Test
  public void shouldConvertBeforeSerialization() {
    assertEquals("1", gson.toJson(true));
    assertEquals("0", gson.toJson(false));
  }

  @Test
  public void shouldReverseConversionBeforeAfterDeserialization() {
    assertEquals(true, gson.fromJson("1", Boolean.class));
    assertEquals(false, gson.fromJson("0", Boolean.class));
  }

}