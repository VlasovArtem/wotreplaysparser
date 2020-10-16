package org.avlasov.parser.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

public class MatchLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private final DateTimeFormatter dateTimeFormatter;

    public MatchLocalDateTimeSerializer() {
        dateTimeFormatter = new DateTimeFormatterBuilder()
                .appendValue(DAY_OF_MONTH, 2)
                .appendLiteral('.')
                .appendValue(MONTH_OF_YEAR, 2)
                .appendLiteral('.')
                .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .appendLiteral(' ')
                .append(DateTimeFormatter.ISO_TIME)
                .toFormatter();
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(dateTimeFormatter.format(value));
    }
}
