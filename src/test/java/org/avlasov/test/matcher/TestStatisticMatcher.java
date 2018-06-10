package org.avlasov.test.matcher;

import org.avlasov.test.TestStatistic;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Ignore;

import java.util.Objects;

/**
 * Created By artemvlasov on 10/06/2018
 **/
@Ignore
public class TestStatisticMatcher extends BaseMatcher<TestStatistic> {

    private final TestStatistic expected;

    public TestStatisticMatcher(TestStatistic expected) {
        this.expected = Objects.requireNonNull(expected);
    }

    public static TestStatisticMatcher matcher(TestStatistic expected) {
        return new TestStatisticMatcher(expected);
    }

    @Override
    public boolean matches(Object actual) {
        if (actual instanceof TestStatistic) {
            TestStatistic actualData = (TestStatistic) actual;
            return actualData.getTotalDamageDealt() == expected.getTotalDamageDealt()
                    && actualData.getTotalFrags() == expected.getTotalFrags()
                    && actualData.getTotalScore() == expected.getTotalScore();
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("TestStatistic is not matching");
    }
}
