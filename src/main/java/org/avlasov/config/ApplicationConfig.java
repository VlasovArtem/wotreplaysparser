package org.avlasov.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.avlasov.chucktournament.config.ChuckTournamentConfig;
import org.avlasov.config.properties.ApplicationProperties;
import org.avlasov.parser.config.ParserConfig;
import org.avlasov.statistic.config.StatisticConfig;
import org.avlasov.wotapi.config.WotApiConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties({ApplicationProperties.class})
@Import({
        ChuckTournamentConfig.class,
        ParserConfig.class,
        StatisticConfig.class,
        WotApiConfig.class,
})
public class ApplicationConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
