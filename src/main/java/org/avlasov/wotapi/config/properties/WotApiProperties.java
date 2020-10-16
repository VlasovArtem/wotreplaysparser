package org.avlasov.wotapi.config.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wot-api")
@Data
@NoArgsConstructor
public class WotApiProperties {

    private String applicationId;
    private String wotApiDataInformationFilename;
    private String tankopediaApiUrl;
    private String tankopediaDataFilename;
    private String vehiclesApiUrl;
    private String vehiclesDataFilename;
    private String arenasApiUrl;
    private String arenasDataFilename;

}
