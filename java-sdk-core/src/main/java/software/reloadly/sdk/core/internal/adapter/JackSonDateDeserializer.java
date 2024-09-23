package software.reloadly.sdk.core.internal.adapter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class JackSonDateDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            return Date.from(
                    Instant.from(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm[:ss[Z]]")
                                    .parse(jsonParser.getText())));
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }
    }
}
