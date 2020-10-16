package org.avlasov.parser.builder;

import org.avlasov.parser.config.properties.WotReplaysProperties;
import org.springframework.stereotype.Component;

@Component
public class WotReplaysLinkUtils {

    private final WotReplaysProperties wotReplaysProperties;

    public WotReplaysLinkUtils(WotReplaysProperties wotReplaysProperties) {
        this.wotReplaysProperties = wotReplaysProperties;
    }

    public WotReplaysLinkBuilder getBuilder() {
        return new WotReplaysLinkBuilder(wotReplaysProperties);
    }


}
