package org.avlasov.parser.entity.statistic;

import lombok.Builder;
import lombok.Getter;

/**
 * Created By artemvlasov on 22/05/2018
 **/
@Getter
public class MatchesStatistic extends AbstractStatistic {

    private int totalPlayedMatches;
    private long totalPlayedTimeInSeconds;
    private int totalWins;

    @Builder
    public MatchesStatistic(int totalDamageDealt, int totalFrags, int totalScore, int totalPlayedMatches, long totalPlayedTimeInSeconds, int totalWins) {
        super(totalDamageDealt, totalFrags, totalScore);
        this.totalPlayedMatches = totalPlayedMatches;
        this.totalPlayedTimeInSeconds = totalPlayedTimeInSeconds;
        this.totalWins = totalWins;
    }
}
