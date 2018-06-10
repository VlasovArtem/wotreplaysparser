package org.avlasov.test.matcher;

import org.avlasov.entity.match.Match;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Ignore;

import java.util.Objects;

/**
 * Created By artemvlasov on 10/06/2018
 **/
@Ignore
public class MatchMatcher extends BaseMatcher<Match> {

    private final Match expected;

    public MatchMatcher(Match expected) {
        Objects.requireNonNull(expected);
        this.expected = expected;
    }

    public static MatchMatcher match(Match expected) {
        return new MatchMatcher(expected);
    }

    @Override
    public boolean matches(Object actual) {
        if (actual instanceof Match) {
            Match expectedMatch = (Match) actual;
            return Objects.equals(expectedMatch.getResult(), this.expected.getResult())
                    && Objects.equals(expectedMatch.getMapData(), this.expected.getMapData())
                    && Objects.equals(expectedMatch.getPlatoon(), this.expected.getPlatoon())
                    && Objects.equals(expectedMatch.getPlayerMatches(), this.expected.getPlayerMatches())
                    && expectedMatch.getMatchDurationInSeconds() == this.expected.getMatchDurationInSeconds()
                    && Objects.equals(expectedMatch.getMatchLink(), this.expected.getMatchLink());
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("Is not matching matches");
    }

}
