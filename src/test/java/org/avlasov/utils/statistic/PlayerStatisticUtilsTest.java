package org.avlasov.utils.statistic;

import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.PlayerMatch;
import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.entity.statistic.PlayerStatistic;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.avlasov.test.TestEntitiesCreator.*;
import static org.junit.Assert.*;

/**
 * Created By artemvlasov on 10/06/2018
 **/
public class PlayerStatisticUtilsTest {

    private PlayerStatisticUtils playerStatisticUtils;

    public PlayerStatisticUtilsTest() {
        playerStatisticUtils = new PlayerStatisticUtils();
    }

    @Test
    public void getPlayerStatistics_WithValidData_ReturnPlayerStatisticList() {
        List<PlayerStatistic> playerStatistics = playerStatisticUtils.calculateStatistic(singletonList(getBestMatch()));
        assertNotNull(playerStatistics);
        assertThat(playerStatistics, IsCollectionWithSize.hasSize(1));
        verifyPlayerStatistic(playerStatistics.get(0));
    }

    @Test
    public void getPlayerStatistics_WithNullMatches_ReturnEmptyList() {
        List<PlayerStatistic> playerStatistics = playerStatisticUtils.calculateStatistic(null);
        assertNotNull(playerStatistics);
        assertThat(playerStatistics, IsEmptyCollection.empty());
    }

    @Test
    public void getPlayerStatistics_WithEmptyMatches_ReturnEmptyList() {
        List<PlayerStatistic> playerStatistics = playerStatisticUtils.calculateStatistic(Collections.emptyList());
        assertNotNull(playerStatistics);
        assertThat(playerStatistics, IsEmptyCollection.empty());
    }

    private void verifyPlayerStatistic(PlayerStatistic playerStatistic) {
        PlayerMatch playerMatch = getBestPlayerMatch(DrawGroup.FIRST);
        assertNotNull(playerStatistic);
        assertEquals(playerStatistic.getPlayer(), getPlayer(DrawGroup.FIRST));
        Match match = playerStatistic.getMatches().get(0);
        Match generatedMatch = getBestMatch();
        assertEquals(generatedMatch.getResult(), match.getResult());
        assertEquals(generatedMatch.getPlayerMatches(), match.getPlayerMatches());
        assertEquals(generatedMatch.getPlatoon(), match.getPlatoon());
        assertEquals(generatedMatch.getMapData(), match.getMapData());
        assertEquals(generatedMatch.getMatchDurationInSeconds(), match.getMatchDurationInSeconds());
        assertEquals(generatedMatch.getMatchLink(), match.getMatchLink());
        assertEquals(playerStatistic.getPlatoon(), getPlatoon());
        assertEquals(playerStatistic.getTotalScore(), playerMatch.getDamage() + playerMatch.getFrags() * 300);
        assertEquals(playerStatistic.getTotalFrags(), playerMatch.getFrags());
        assertEquals(playerStatistic.getTotalDamageDealt(), playerMatch.getDamage());
        assertEquals(playerStatistic.getAverageDamageDealt(), playerMatch.getDamage(), 0);
        assertEquals(playerStatistic.getAverageFrags(), playerMatch.getFrags(), 0);
        assertEquals(playerStatistic.getAverageScore(), playerMatch.getDamage() + playerMatch.getFrags() * 300, 0);
    }
}