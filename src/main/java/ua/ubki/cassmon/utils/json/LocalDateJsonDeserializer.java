package ua.ubki.cassmon.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;
import ua.ubki.cassmon.utils.DateUtils;

import java.io.IOException;
import java.time.LocalDate;

public class LocalDateJsonDeserializer extends JsonDeserializer<LocalDate> {
    @Override
    public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String dateString = parser.getText();
        if (StringUtils.isBlank(dateString)) {
            return null;
        } else {
            return DateUtils.smartParse(dateString).toLocalDate();
        }
    }
}
