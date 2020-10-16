package org.avlasov.parser.entity.statistic;

import org.avlasov.chucktournament.entity.match.Player;

/**
 * Created By artemvlasov on 29/05/2018
 **/
public class DrawGroupPlayerStatistic extends AbstractStatistic {

    private Player player;

    public DrawGroupPlayerStatistic(int totalDamageDealt, int totalFrags, int totalScore, Player player) {
        super(totalDamageDealt, totalFrags, totalScore);
        this.player = player;
    }

    public DrawGroupPlayerStatistic(PlayerStatistic playerStatistic) {
        this(playerStatistic.getTotalDamageDealt(), playerStatistic.getTotalFrags(), playerStatistic.getTotalScore(), playerStatistic.getPlayer());
    }
}
