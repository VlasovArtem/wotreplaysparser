package org.avlasov.utils.statistic;

import org.avlasov.entity.statistic.ChampionshipStatistic;
import org.avlasov.test.TestEntitiesCreator;
import org.avlasov.utils.MatchUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created By artemvlasov on 10/06/2018
 **/
public class ChampionshipStatisticUtilsTest {

    private ChampionshipStatisticUtils championshipStatisticUtils;

    @Before
    public void setUp() throws Exception {
        championshipStatisticUtils = ChampionshipStatisticUtils.builder()
                .matchesStatisticUtils(new MatchesStatisticUtils())
                .matchStatisticUtils(new MatchStatisticUtils())
                .drawGroupPlayerStatisticUtils(new DrawGroupPlayerStatisticUtils())
                .matchUtils(new MatchUtils())
                .platoonStatisticUtils(new PlatoonStatisticUtils())
                .playerStatisticUtils(new PlayerStatisticUtils())
                .vehicleStatisticUtils(new VehicleStatisticUtils())
                .build();
    }

    @Test
    public void calculateStatistic_WithValidData_ReturnChampionshipStatistic() {
        Optional<ChampionshipStatistic> championshipStatistic = championshipStatisticUtils.calculateStatistic(List.of(TestEntitiesCreator.getBestMatch()));
        assertTrue(championshipStatistic.isPresent());
    }

    @Test
    public void calculateStatistic_WithNullMatches_ReturnEmptyOptional() {
        Optional<ChampionshipStatistic> championshipStatistic = championshipStatisticUtils.calculateStatistic(null);
        assertFalse(championshipStatistic.isPresent());
    }

    @Test
    public void calculateStatistic_WithEmptyMatches_ReturnEmptyOptional() {
        Optional<ChampionshipStatistic> championshipStatistic = championshipStatisticUtils.calculateStatistic(List.of());
        assertFalse(championshipStatistic.isPresent());
    }

}