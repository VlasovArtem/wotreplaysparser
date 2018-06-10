package org.avlasov.utils;

import org.avlasov.entity.match.Match;
import org.avlasov.test.matcher.MatchMatcher;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.avlasov.test.TestEntitiesCreator.getBestMatch;
import static org.avlasov.test.TestEntitiesCreator.getWorstMatch;
import static org.junit.Assert.*;

/**
 * Created By artemvlasov on 10/06/2018
 **/
public class MatchUtilsTest {

    private final MatchUtils matchUtils;

    public MatchUtilsTest() {
        matchUtils = new MatchUtils();
    }

    @Test
    public void findBestMatch_WithValidData_ReturnMatch() {
        Match bestMatch = getBestMatch();
        Optional<Match> bestMatchData = matchUtils.findBestMatch(List.of(bestMatch, getWorstMatch()));
        assertTrue(bestMatchData.isPresent());
        assertThat(bestMatchData.get(), MatchMatcher.match(bestMatch));
    }

    @Test
    public void findBestMatch_WithNullList_ReturnEmptyOptional() {
        Optional<Match> bestMatch = matchUtils.findBestMatch(null);
        assertFalse(bestMatch.isPresent());
    }

    @Test
    public void findBestMatch_WithEmptyList_ReturnEmptyOptional() {
        Optional<Match> bestMatch = matchUtils.findBestMatch(List.of());
        assertFalse(bestMatch.isPresent());
    }

    @Test
    public void findWorstMatch_WithValidData_ReturnMatch() {
        Match match = getWorstMatch();
        Optional<Match> bestMatch = matchUtils.findWorstMatch(List.of(match, getBestMatch()));
        assertTrue(bestMatch.isPresent());
        assertThat(bestMatch.get(), MatchMatcher.match(match));
    }

    @Test
    public void findWorstMatch_WithNullList_ReturnEmptyOptional() {
        Optional<Match> bestMatch = matchUtils.findWorstMatch(null);
        assertFalse(bestMatch.isPresent());
    }

    @Test
    public void findWorstMatch_WithEmptyList_ReturnEmptyOptional() {
        Optional<Match> bestMatch = matchUtils.findWorstMatch(List.of());
        assertFalse(bestMatch.isPresent());
    }

}