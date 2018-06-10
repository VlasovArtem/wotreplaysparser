package org.avlasov.utils.statistic;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Created By artemvlasov on 09/06/2018
 **/
public class AverageStatisticUtilsTest {

    @Test
    public void calculateAverage() {
        assertEquals(33.33, AverageStatisticUtils.calculateAverage(100, 3), 0);
    }

}