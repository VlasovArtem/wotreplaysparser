package org.avlasov.chucktournament.config.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.avlasov.chucktournament.entity.match.Platoon;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "platoons")
@Data
@NoArgsConstructor
@Component
public class PlatoonsProperties {

    private List<Platoon> platoons;

}
