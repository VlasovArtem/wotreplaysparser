package org.avlasov.config.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
@Data
@NoArgsConstructor
public class ApplicationProperties {

    private String supportedWotVersion;

}
