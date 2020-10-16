package org.avlasov.chucktournament.config.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "tournament-config")
@Data
@NoArgsConstructor
@Component
public class TournamentProperties {

    private int maxPlatoonPlayers;
    private int requiredNumberOfReplays;

}
