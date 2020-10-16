package org.avlasov.parser.replay.entity.match.details;

import lombok.Data;

import java.util.Map;

@Data
public class ServerSettings {

    private Object[] roaming;
    private Map<String, Boolean> spgRedesignFeatures;
    private boolean isPotapovQuestEnabled;

}
