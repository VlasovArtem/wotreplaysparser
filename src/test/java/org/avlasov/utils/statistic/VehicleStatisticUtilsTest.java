package org.avlasov.utils.statistic;

import org.avlasov.entity.match.PlayerMatch;
import org.avlasov.entity.statistic.VehicleStatistic;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.avlasov.entity.match.enums.DrawGroup.FIRST;
import static org.avlasov.test.TestEntitiesCreator.getBestMatch;
import static org.avlasov.test.TestEntitiesCreator.getBestPlayerMatch;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created By artemvlasov on 09/06/2018
 **/
public class VehicleStatisticUtilsTest {

    private VehicleStatisticUtils vehicleStatisticUtils;

    public VehicleStatisticUtilsTest() {
        this.vehicleStatisticUtils = new VehicleStatisticUtils();
    }

    @Test
    public void getVehicleStatistics_WithValidData_ReturnVehicleStatisticList() {
        List<VehicleStatistic> vehicleStatistics = vehicleStatisticUtils.calculateStatistic(List.of(getBestMatch()));
        assertThat(vehicleStatistics, IsCollectionWithSize.hasSize(1));
        verifyVehicleStatistic(vehicleStatistics.get(0));
    }

    @Test
    public void getVehicleStatistics_WithNullMatches_ReturnEmptyList() {
        List<VehicleStatistic> vehicleStatistics = vehicleStatisticUtils.calculateStatistic(null);
        assertThat(vehicleStatistics, IsEmptyCollection.empty());
    }

    @Test
    public void getVehicleStatistics_WithEmptyMatches_ReturnEmptyList() {
        List<VehicleStatistic> vehicleStatistics = vehicleStatisticUtils.calculateStatistic(Collections.emptyList());
        assertThat(vehicleStatistics, IsEmptyCollection.empty());
    }

    private void verifyVehicleStatistic(VehicleStatistic vehicleStatistic) {
        PlayerMatch playerMatch = getBestPlayerMatch(FIRST);
        assertEquals(playerMatch.getVehicleName(), vehicleStatistic.getVehicleName());
        assertEquals(1, vehicleStatistic.getNumberOfMatches());
        assertEquals(playerMatch.getDamage() + playerMatch.getFrags() * 300, vehicleStatistic.getTotalScore());
        assertEquals(playerMatch.getDamage(), vehicleStatistic.getTotalDamageDealt());
        assertEquals(playerMatch.getFrags(), vehicleStatistic.getTotalFrags());
        assertEquals(playerMatch.getDamage(), vehicleStatistic.getAverageDamageDealt(), 0);
        assertEquals(playerMatch.getFrags(), vehicleStatistic.getAverageFrags(), 0);
        assertEquals(playerMatch.getDamage() + playerMatch.getFrags() * 300, vehicleStatistic.getAverageScore(), 0);
    }
}