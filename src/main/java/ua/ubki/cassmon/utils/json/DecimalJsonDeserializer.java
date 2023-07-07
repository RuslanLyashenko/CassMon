package ua.ubki.cassmon.utils.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;

public class DecimalJsonDeserializer extends JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String decimalString = parser.getText();
        if (StringUtils.isBlank(decimalString)) {
            return null;
        }
        if (decimalString.contains(",")) {
            decimalString = decimalString.replace(",", ".");
        }
        return new BigDecimal(decimalString);
    }
}
