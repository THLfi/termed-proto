package fi.thl.termed.util;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Spring MessageConverter for converting JSON using Gson.
 */
public final class GsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

  private Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public GsonHttpMessageConverter() {
    super(new MediaType("application", "json", Charsets.UTF_8));
  }

  @Override
  protected Object readInternal(Class<?> c, HttpInputMessage inputMessage)
      throws IOException {
    return gson.fromJson(CharStreams.toString(
        new InputStreamReader(inputMessage.getBody(), Charsets.UTF_8)), c);
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return true;
  }

  @Override
  protected void writeInternal(Object t, HttpOutputMessage outputMessage)
      throws IOException {
    outputMessage.getBody().write(gson.toJson(t).getBytes(Charsets.UTF_8));
  }

}
