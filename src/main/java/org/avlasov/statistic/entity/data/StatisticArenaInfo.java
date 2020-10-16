package org.avlasov.statistic.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticArenaInfo implements Comparable<StatisticArenaInfo> {

    private StatisticArena statisticArena;
    private int battles;
    private int wins;
    private int lose;
    private double efficiency;
    private double winPercentage;
    private double fragAverage;
    private double damageDealtAverage;
    private double xpAverage;
    private double spottedAverage;
    private double damageAssistedAverage;
    private double droppedCapturePointsAverage;

    @Override
    public int compareTo(StatisticArenaInfo o) {
        return Double.compare(efficiency, o.getEfficiency());
    }
}
