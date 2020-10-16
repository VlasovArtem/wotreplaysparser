package org.avlasov.parser.replay.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.avlasov.parser.replay.entity.match.MatchResult;
import org.avlasov.parser.replay.entity.match.details.MatchPlayerDetails;

@Data
@EqualsAndHashCode(of = "matchResult")
public class Replay {

    private MatchPlayerDetails matchPlayerDetails;
    private MatchResult matchResult;
    private WotReplay wotReplay;

}
