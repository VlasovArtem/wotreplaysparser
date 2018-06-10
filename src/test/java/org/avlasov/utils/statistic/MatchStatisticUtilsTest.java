package org.avlasov.utils.statistic;

import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.PlayerMatch;
import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.entity.statistic.MatchStatistic;
import org.avlasov.test.matcher.MatchMatcher;
import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.avlasov.test.TestEntitiesCreator.getBestMatch;
import static org.avlasov.test.TestEntitiesCreator.getBestPlayerMatch;
import static org.junit.Assert.*;

/**
 * Created By artemvlasov on 10/06/2018
 **/
public class MatchStatisticUtilsTest {

    private MatchStatisticUtils matchStatisticUtils;

    public MatchStatisticUtilsTest() {
        matchStatisticUtils = new MatchStatisticUtils();
    }

    @Test
    public void calculateStatistic_WithValidData_ReturnMatchStatistic() {
        Optional<MatchStatistic> matchStatistic = matchStatisticUtils.calculateStatistic(Collections.singletonList(getBestMatch()));
        assertTrue(matchStatistic.isPresent());
        verifyMatchStatistic(matchStatistic.get());
    }

    @Test
    public void calculateStatistic_WithNullMatches_ReturnEmptyOptional() {
        Optional<MatchStatistic> matchStatistic = matchStatisticUtils.calculateStatistic(null);
        assertFalse(matchStatistic.isPresent());
    }

    @Test
    public void calculateStatistic_WithEmptyMatches_ReturnEmptyOptional() {
        Optional<MatchStatistic> matchStatistic = matchStatisticUtils.calculateStatistic(Collections.emptyList());
        assertFalse(matchStatistic.isPresent());
    }

    private void verifyMatchStatistic(MatchStatistic matchStatistic) {
        Match match = getBestMatch();
        PlayerMatch playerMatch = getBestPlayerMatch(DrawGroup.FIRST);
        verifyPlayerMatch(matchStatistic.getTop10DamageDealtPlayerMatch(), 1, playerMatch);
        verifyPlayerMatch(matchStatistic.getTop10FragsPlayerMatch(), 1, playerMatch);
        verifyMatches(matchStatistic.getTop10PlatoonMaxDamageDealtMatches(), 1, match);
        verifyMatches(matchStatistic.getTop10PlatoonMaxFragsMatches(), 1, match);
        verifyMatches(matchStatistic.getTop10PlatoonMaxScoreMatches(), 1, match);
    }

    private void verifyMatches(List<Match> matches, int size, Match expectedFirstElement) {
        assertNotNull(matches);
        if (size > 0) {
            assertNotNull(expectedFirstElement);
            assertThat(matches, IsCollectionWithSize.hasSize(size));
            assertThat(matches.get(0), MatchMatcher.match(expectedFirstElement));
        } else {
            assertThat(matches, IsEmptyCollection.empty());
        }
    }

    private void verifyPlayerMatch(List<PlayerMatch> playerMatches, int size, PlayerMatch expectedFirstElement) {
        assertNotNull(playerMatches);
        if (size > 0) {
            assertNotNull(expectedFirstElement);
            assertThat(playerMatches, IsCollectionWithSize.hasSize(size));
            assertEquals(expectedFirstElement, playerMatches.get(0));
        } else {
            assertThat(playerMatches, IsEmptyCollection.empty());
        }
    }

}