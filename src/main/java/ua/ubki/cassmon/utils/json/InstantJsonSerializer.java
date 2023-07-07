package ua.ubki.cassmon.utils.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class InstantJsonSerializer extends JsonSerializer<Instant> {
    DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneOffset.UTC);

    @Override
    public void serialize(Instant value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        String str = formatter.format(value);
        generator.writeString(str);
    }
}
