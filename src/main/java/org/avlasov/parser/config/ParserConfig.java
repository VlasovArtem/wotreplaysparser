package org.avlasov.parser.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.avlasov.parser.deserializer.MatchLocalDateTimeDeserializer;
import org.avlasov.parser.serializer.MatchLocalDateTimeSerializer;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

@Configuration
@ComponentScan("org.avlasov.parser")
public class ParserConfig {

    @Bean
    public DateTimeFormatter matchDateTimeFormatter() {
        return new DateTimeFormatterBuilder()
                .appendValue(YEAR, 4)
                .appendLiteral('-')
                .appendValue(MONTH_OF_YEAR, 2)
                .appendLiteral('-')
                .appendValue(DAY_OF_MONTH, 2)
                .appendLiteral(' ')
                .appendValue(HOUR_OF_DAY, 2)
                .appendLiteral(':')
                .appendValue(MINUTE_OF_HOUR, 2)
                .toFormatter();
    }

    @Bean
    public PhantomJSDriver phantomJSDriver() {
        System.setProperty("phantomjs.binary.path", "libs/phantomjs");
        DesiredCapabilities dcap = new DesiredCapabilities();
        String[] phantomArgs = new String[]{
                "--webdriver-loglevel=NONE"
        };
        dcap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, phantomArgs);
        return new PhantomJSDriver(dcap);
    }

    @Bean
    public ObjectMapper wotReplayParserObjectMapper() {
        return Jackson2ObjectMapperBuilder
                .json()
                .failOnUnknownProperties(false)
                .modules(new JavaTimeModule())
                .deserializerByType(LocalDateTime.class, new MatchLocalDateTimeDeserializer())
                .serializerByType(LocalDateTime.class, new MatchLocalDateTimeSerializer())
                .build();
    }

    @Bean
    public RestTemplate wotReplayParserRestTemplate() {
        return new RestTemplateBuilder().build();
    }

}
