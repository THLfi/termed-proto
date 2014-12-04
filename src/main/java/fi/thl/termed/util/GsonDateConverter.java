package fi.thl.termed.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Serializer and deserializer for ISO8601-formatted date using JodaTime.
 */
public class GsonDateConverter implements JsonSerializer<Date>,
        JsonDeserializer<Date> {

    private final DateFormat localFormat =
            new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final DateFormat defaultFormat = new SimpleDateFormat();

    @Override
    public JsonElement serialize(Date date, Type srcType,
            JsonSerializationContext context) {
        return date != null ? new JsonPrimitive(new DateTime(date).toString())
                            : new JsonPrimitive("");
    }

    @Override
    public Date deserialize(JsonElement json, Type type,
            JsonDeserializationContext context) {
        String date = json.getAsString();

        if (date.isEmpty()) {
            return null;
        }

        try {
            return new DateTime(date).toDate();
        } catch (IllegalArgumentException e) {
            // ignore
        }

        try {
            return localFormat.parse(date);
        } catch (ParseException e) {
            // ignore
        }

        try {
            return defaultFormat.parse(date);
        } catch (ParseException e) {
            throw new JsonSyntaxException(date, e);
        }
    }

}
