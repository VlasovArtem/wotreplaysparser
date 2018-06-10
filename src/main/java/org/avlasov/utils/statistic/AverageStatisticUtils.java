package org.avlasov.utils.statistic;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created By artemvlasov on 01/06/2018
 **/
public class AverageStatisticUtils {

    public static double calculateAverage(double total, double numberOfIterations) {
        return new BigDecimal(total / numberOfIterations).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

}
