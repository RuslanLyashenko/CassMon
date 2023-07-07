package ua.ubki.cassmon.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class InstantJsonDeserializer extends JsonDeserializer<Instant> {
    DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneOffset.UTC);

    @Override
    public Instant deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        if (parser == null || parser.getText().isEmpty()) {
            return null;
        } else {
            return Instant.from(formatter.parse(parser.getText()));
        }
    }
}