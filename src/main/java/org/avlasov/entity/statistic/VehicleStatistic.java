package org.avlasov.entity.statistic;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created By artemvlasov on 22/05/2018
 * Without match win
 **/
@Getter
public class VehicleStatistic extends AbstractStatistic {

    private String vehicleName;
    private int numberOfMatches;
    private double averageDamageDealt;
    private double averageFrags;

    @Builder
    public VehicleStatistic(int totalDamageDealt, int totalFrags, int totalScore, String vehicleName, int numberOfMatches) {
        super(totalDamageDealt, totalFrags, totalScore);
        this.vehicleName = vehicleName;
        this.numberOfMatches = numberOfMatches;
        this.averageDamageDealt = new BigDecimal((double) totalDamageDealt / (double) numberOfMatches).setScale(2, RoundingMode.HALF_UP).doubleValue();
        this.averageFrags = new BigDecimal((double) totalFrags / (double) numberOfMatches).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}
