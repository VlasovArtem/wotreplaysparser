package org.avlasov.parser.config.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "wot-replays")
@Data
@NoArgsConstructor
@Component
public class WotReplaysProperties {

    private String link;
    private String downloadLinkBase;
    private String downloadLinkGetPattern;
    private String downloadLinkPattern;
    private String replayLinkGetPattern;
    private String replayLinkPattern;
    private String searchLinkPattern;
    private String searchLink;
    private String allPageReplaysDataFilename;
    private String searchLinkReplaysDataFilename;
    private Map<String, Integer> wotVersionsMapper;

}
