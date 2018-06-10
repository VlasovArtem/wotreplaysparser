package org.avlasov.entity.statistic;

import lombok.Builder;
import lombok.Getter;
import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.Platoon;
import org.avlasov.entity.match.Player;

import java.util.List;

import static org.avlasov.utils.statistic.AverageStatisticUtils.calculateAverage;

/**
 * Created By artemvlasov on 22/05/2018
 * total score field will not contains score from match win
 **/
@Getter
public class PlayerStatistic extends AbstractAverageStatistic {

    private List<Match> matches;
    private Player player;
    private Platoon platoon;

    @Builder
    public PlayerStatistic(int totalDamageDealt, int totalFrags, int totalScore, List<Match> matches, Player player, Platoon platoon) {
        super(totalDamageDealt, totalFrags, totalScore,
                calculateAverage(totalDamageDealt, matches.size()),
                calculateAverage(totalFrags, matches.size()),
                calculateAverage(totalScore, matches.size()));
        this.matches = matches;
        this.player = player;
        this.platoon = platoon;
    }




}
