package org.avlasov.statistic.config;

import org.avlasov.parser.config.ParserConfig;
import org.avlasov.wotapi.config.WotApiConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("org.avlasov.statistic.config")
@Import({ParserConfig.class, WotApiConfig.class})
public class StatisticConfig {



}
