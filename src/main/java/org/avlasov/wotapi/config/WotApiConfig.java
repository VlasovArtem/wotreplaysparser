package org.avlasov.wotapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.avlasov.wotapi.config.properties.WotApiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan("org.avlasov.wotapi")
@EnableConfigurationProperties({WotApiProperties.class})
public class WotApiConfig {

    @Bean
    public RestTemplate wotApiRestTemplate() {
        return new RestTemplateBuilder()
                .additionalMessageConverters(mappingJackson2HttpMessageConverter())
                .build();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter(wotApiObjectMapper());
    }

    @Bean
    public ObjectMapper wotApiObjectMapper() {
        return Jackson2ObjectMapperBuilder
                .json()
                .failOnUnknownProperties(false)
                .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .modules(new JavaTimeModule())
                .build();
    }

}
