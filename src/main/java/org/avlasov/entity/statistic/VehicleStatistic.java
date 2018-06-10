package org.avlasov.entity.statistic;

import lombok.Builder;
import lombok.Getter;

import static org.avlasov.utils.statistic.AverageStatisticUtils.calculateAverage;

/**
 * Created By artemvlasov on 22/05/2018
 * Without match win
 **/
@Getter
public class VehicleStatistic extends AbstractAverageStatistic {

    private String vehicleName;
    private int numberOfMatches;

    @Builder
    public VehicleStatistic(int totalDamageDealt, int totalFrags, int totalScore, String vehicleName, int numberOfMatches) {
        super(totalDamageDealt, totalFrags, totalScore,
                calculateAverage(totalDamageDealt, numberOfMatches),
                calculateAverage(totalFrags, numberOfMatches),
                calculateAverage(totalScore, numberOfMatches));
        this.vehicleName = vehicleName;
        this.numberOfMatches = numberOfMatches;
    }



}
