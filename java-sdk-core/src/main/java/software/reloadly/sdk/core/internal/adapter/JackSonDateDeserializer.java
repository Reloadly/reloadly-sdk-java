package software.reloadly.sdk.core.internal.adapter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class JackSonDateDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        try {
            TemporalAccessor temporalAccessor = DateTimeFormatter.ofPattern("yyyy-MM-dd[ ]['T']HH:mm[:ss][X]")
                    .parseBest(jsonParser.getText(), OffsetDateTime::from, LocalDateTime::from);

            if (temporalAccessor instanceof OffsetDateTime) {
                return Date.from(((OffsetDateTime) temporalAccessor).toInstant());
            } else if (temporalAccessor instanceof LocalDateTime) {
                return Date.from(((LocalDateTime) temporalAccessor).atZone(ZoneId.of("UTC")).toInstant());
            } else {
                return Date.from(Instant.from(temporalAccessor));
            }
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }
    }
}
