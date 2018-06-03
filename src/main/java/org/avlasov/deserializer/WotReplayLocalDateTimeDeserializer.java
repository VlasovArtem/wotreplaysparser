package org.avlasov.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Created By artemvlasov on 02/06/2018
 **/
public class WotReplayLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private final static DateTimeFormatter DATE_TIME_FORMATTER;

    static {
        DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
                .appendValue(ChronoField.DAY_OF_MONTH)
                .appendLiteral('.')
                .appendValue(ChronoField.MONTH_OF_YEAR)
                .appendLiteral('.')
                .appendValue(ChronoField.YEAR)
                .appendLiteral(' ')
                .append(DateTimeFormatter.ISO_LOCAL_TIME)
                .toFormatter();

    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String valueAsString = p.getValueAsString();
        return LocalDateTime.parse(valueAsString, DATE_TIME_FORMATTER);
    }

}
