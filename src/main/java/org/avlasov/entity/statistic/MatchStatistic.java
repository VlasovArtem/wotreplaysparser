package org.avlasov.entity.statistic;

import lombok.Builder;
import lombok.Getter;
import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.PlayerMatch;

import java.util.List;

/**
 * Created By artemvlasov on 01/06/2018
 **/
@Builder
@Getter
public class MatchStatistic {

    private List<Match> top10PlatoonMaxDamageDealtMatches;
    private List<Match> top10PlatoonMaxScoreMatches;
    private List<Match> top10PlatoonMaxFragsMatches;
    private List<PlayerMatch> top10DamageDealtPlayerMatch;
    private List<PlayerMatch> top10FragsPlayerMatch;

}
