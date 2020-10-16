package org.avlasov.chucktournament.entity.match;

import lombok.*;
import org.avlasov.chucktournament.entity.match.enums.Result;

/**
 * Created By artemvlasov on 22/05/2018
 **/
@Builder
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {

    private Result result;
    private int matchPlatoonDamageDealt;
    private int matchPlatoonFrags;
    private int matchScore;

}
