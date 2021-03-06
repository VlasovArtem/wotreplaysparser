package org.avlasov.parser.entity.statistic;

import lombok.Builder;
import org.avlasov.chucktournament.entity.match.Match;
import org.avlasov.chucktournament.entity.match.PlayerMatch;

import java.util.List;

/**
 * Created By artemvlasov on 01/06/2018
 **/
@Builder
public class MatchStatistic {

    private List<Match> top10PlatoonMaxDamageDealtMatches;
    private List<Match> top10PlatoonMaxScoreMatches;
    private List<Match> top10PlatoonMaxFragsMatches;
    private List<PlayerMatch> top10DamageDealtPlayerMatch;
    private List<PlayerMatch> top10FragsPlayerMatch;

}
