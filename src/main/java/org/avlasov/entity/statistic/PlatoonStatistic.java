package org.avlasov.entity.statistic;

import lombok.Builder;
import lombok.Getter;
import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.Platoon;

import java.util.List;

/**
 * Created By artemvlasov on 22/05/2018
 **/
@Getter
public class PlatoonStatistic extends AbstractStatistic {

    private Platoon platoon;
    private List<Match> matches;
    private List<PlatoonPlayerStatistic> platoonPlayerStatistic;
    private int totalPlatoonPlayedMatches;

    @Builder
    public PlatoonStatistic(int totalDamageDealt, int totalFrags, int totalScore, Platoon platoon, List<Match> matches, List<PlatoonPlayerStatistic> platoonPlayerStatistic, int totalPlatoonPlayedMatches) {
        super(totalDamageDealt, totalFrags, totalScore);
        this.platoon = platoon;
        this.matches = matches;
        this.platoonPlayerStatistic = platoonPlayerStatistic;
        this.totalPlatoonPlayedMatches = totalPlatoonPlayedMatches;
    }
}
