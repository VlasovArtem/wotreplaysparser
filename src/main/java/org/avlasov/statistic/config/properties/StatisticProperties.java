package org.avlasov.statistic.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties("statistic")
@Component
public class StatisticProperties {

    private String vehicleArenaStatisticFilename;
    private String vehicleTypeArenaStatisticFilename;
    private String vehicleTypeArenaStatisticFilenamePattern;
    private String vehicleArenaStatisticFilenamePattern;

}
