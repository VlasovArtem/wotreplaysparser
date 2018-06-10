package org.avlasov.utils.statistic;

import org.avlasov.entity.match.PlayerMatch;
import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.entity.match.enums.Result;
import org.avlasov.entity.statistic.PlatoonPlayerStatistic;
import org.avlasov.entity.statistic.PlatoonStatistic;
import org.avlasov.test.TestEntitiesCreator;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.avlasov.test.TestEntitiesCreator.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created By artemvlasov on 10/06/2018
 **/
public class PlatoonStatisticUtilsTest {

    private PlatoonStatisticUtils platoonStatisticUtils;

    public PlatoonStatisticUtilsTest() {
        platoonStatisticUtils = new PlatoonStatisticUtils();
    }

    @Test
    public void getPlatoonStatistics_WithValidData_ReturnPlatoonStatistic() {
        List<PlatoonStatistic> platoonStatistics = platoonStatisticUtils.calculateStatistic(Collections.singletonList(getBestMatch()));
        assertNotNull(platoonStatistics);
        assertThat(platoonStatistics, IsCollectionWithSize.hasSize(1));
        verifyPlatoonStatistics(platoonStatistics);
    }

    @Test
    public void getPlatoonStatistics_WithNullMatches_ReturnEmptyCollection() {
        List<PlatoonStatistic> platoonStatistics = platoonStatisticUtils.calculateStatistic(null);
        assertNotNull(platoonStatistics);
        assertThat(platoonStatistics, IsEmptyCollection.empty());
    }

    @Test
    public void getPlatoonStatistics_WithEmptyMatches_ReturnEmptyCollection() {
        List<PlatoonStatistic> platoonStatistics = platoonStatisticUtils.calculateStatistic(Collections.emptyList());
        assertNotNull(platoonStatistics);
        assertThat(platoonStatistics, IsEmptyCollection.empty());
    }

    private void verifyPlatoonStatistics(List<PlatoonStatistic> platoonStatistics) {
        PlayerMatch playerMatch = TestEntitiesCreator.getBestPlayerMatch(DrawGroup.FIRST);
        PlatoonStatistic platoonStatistic = platoonStatistics.get(0);
        assertNotNull(platoonStatistic);
        assertEquals(getPlatoon(), platoonStatistic.getPlatoon());
        assertEquals(platoonStatistic.getMatches().stream().filter(match -> Result.WIN.equals(match.getResult().getResult())).count(), platoonStatistic.getTotalPlatoonMatchesWins());
        assertEquals(platoonStatistic.getMatches().size(), platoonStatistic.getTotalPlatoonPlayedMatches());
        List<PlatoonPlayerStatistic> platoonPlayerStatistic = platoonStatistic.getPlatoonPlayerStatistic();
        assertThat(platoonPlayerStatistic, IsCollectionWithSize.hasSize(1));
        PlatoonPlayerStatistic pps = platoonPlayerStatistic.get(0);
        assertEquals(getPlatoon(), pps.getPlatoon());
        assertEquals(getPlayer(DrawGroup.FIRST), pps.getPlayer());
        assertEquals(playerMatch.getDamage(), pps.getTotalDamageDealt());
        assertEquals(playerMatch.getFrags(), pps.getTotalFrags());
        assertEquals(playerMatch.getDamage() + playerMatch.getFrags() * 300, pps.getTotalScore());
    }
}