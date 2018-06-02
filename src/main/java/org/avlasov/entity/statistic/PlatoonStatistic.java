package org.avlasov.entity.statistic;

import lombok.Builder;
import lombok.Getter;
import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.Platoon;

import java.util.List;

import static org.avlasov.utils.AverageStatisticUtils.calculateAverage;

/**
 * Created By artemvlasov on 22/05/2018
 **/
@Getter
public class PlatoonStatistic extends AbstractAverageStatistic {

    private Platoon platoon;
    private List<Match> matches;
    private List<PlatoonPlayerStatistic> platoonPlayerStatistic;
    private int totalPlatoonPlayedMatches;
    private final int totalPlatoonMatchesWins;

    @Builder
    public PlatoonStatistic(int totalDamageDealt, int totalFrags, int totalScore, Platoon platoon, List<Match> matches, List<PlatoonPlayerStatistic> platoonPlayerStatistic, int totalPlatoonPlayedMatches, int totalPlatoonMatchesWins) {
        super(totalDamageDealt, totalFrags, totalScore,
                calculateAverage(totalDamageDealt, matches.size()),
                calculateAverage(totalFrags, matches.size()),
                calculateAverage(totalScore, matches.size()));
        this.platoon = platoon;
        this.matches = matches;
        this.platoonPlayerStatistic = platoonPlayerStatistic;
        this.totalPlatoonPlayedMatches = totalPlatoonPlayedMatches;
        this.totalPlatoonMatchesWins = totalPlatoonMatchesWins;
    }

}
