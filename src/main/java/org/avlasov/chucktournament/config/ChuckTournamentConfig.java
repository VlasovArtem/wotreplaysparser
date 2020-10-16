package org.avlasov.chucktournament.config;

import org.avlasov.parser.config.ParserConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ParserConfig.class)
@ComponentScan("org.avlasov.chucktournament")
public class ChuckTournamentConfig {}
