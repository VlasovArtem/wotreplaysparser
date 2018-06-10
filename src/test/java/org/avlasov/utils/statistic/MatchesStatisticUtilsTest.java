package org.avlasov.utils.statistic;

import org.avlasov.entity.match.Match;
import org.avlasov.entity.statistic.MatchesStatistic;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.avlasov.test.TestEntitiesCreator.getBestMatch;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created By artemvlasov on 10/06/2018
 **/
public class MatchesStatisticUtilsTest {

    private MatchesStatisticUtils matchesStatisticUtils;

    public MatchesStatisticUtilsTest() {
        matchesStatisticUtils = new MatchesStatisticUtils();
    }

    @Test
    public void calculateStatistic_WithValidData_ReturnMatchesStatistic() {
        Match match = getBestMatch();
        Optional<MatchesStatistic> matchesStatistic = matchesStatisticUtils.calculateStatistic(Collections.singletonList(match));
        assertTrue(matchesStatistic.isPresent());
        assertEquals(1, matchesStatistic.get().getTotalPlayedMatches());
        assertEquals(1, matchesStatistic.get().getTotalWins());
        assertEquals(match.getMatchDurationInSeconds(), matchesStatistic.get().getTotalPlayedTimeInSeconds());
        assertEquals(match.getResult().getMatchPlatoonDamageDealt(), matchesStatistic.get().getTotalDamageDealt());
        assertEquals(match.getResult().getMatchPlatoonFrags(), matchesStatistic.get().getTotalFrags());
        assertEquals(match.getResult().getMatchScore(), matchesStatistic.get().getTotalScore());
    }

    @Test
    public void calculateStatistic_WithNullMatches_ReturnEmptyOptional() {
        Optional<MatchesStatistic> matchesStatistic = matchesStatisticUtils.calculateStatistic(null);
        assertFalse(matchesStatistic.isPresent());
    }

    @Test
    public void calculateStatistic_WithEmptyMatches_ReturnEmptyOptional() {
        Optional<MatchesStatistic> matchesStatistic = matchesStatisticUtils.calculateStatistic(Collections.emptyList());
        assertFalse(matchesStatistic.isPresent());
    }

}