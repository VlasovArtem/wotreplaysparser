package org.avlasov.entity.statistic;

import lombok.Getter;
import lombok.Setter;

/**
 * Created By artemvlasov on 01/06/2018
 **/
@Getter
@Setter
public abstract class AbstractAverageStatistic extends AbstractStatistic {

    private final double averageDamageDealt;
    private final double averageFrags;
    private final double averageScore;

    public AbstractAverageStatistic(int totalDamageDealt, int totalFrags, int totalScore, double averageDamageDealt, double averageFrags, double averageScore) {
        super(totalDamageDealt, totalFrags, totalScore);
        this.averageDamageDealt = averageDamageDealt;
        this.averageFrags = averageFrags;
        this.averageScore = averageScore;
    }
}
