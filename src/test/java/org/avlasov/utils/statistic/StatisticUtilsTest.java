package org.avlasov.utils.statistic;

import org.avlasov.entity.statistic.AbstractStatistic;
import org.avlasov.test.TestStatistic;
import org.avlasov.test.matcher.TestStatisticMatcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.avlasov.utils.statistic.StatisticUtils.*;
import static org.junit.Assert.*;

/**
 * Created By artemvlasov on 10/06/2018
 **/
public class StatisticUtilsTest {

    @Test
    public void findBestByScore_WithValidData_ReturnBestMatch() {
        TestStatistic bestByScore = findBestByScore(getTestStatistics());
        assertNotNull(bestByScore);
        assertThat(bestByScore, TestStatisticMatcher.matcher(getBestScoreTestStatistic()));
    }

    @Test
    public void findBestByScore_WithNullStatistic_ReturnNull() {
        assertNull(findBestByScore(null));
    }

    @Test
    public void findWorstByScore_WithValidData_ReturnBestMatch() {
        TestStatistic worstByScore = findWorstByScore(getTestStatistics());
        assertNotNull(worstByScore);
        assertThat(worstByScore, TestStatisticMatcher.matcher(getWorstScoreTestStatistic()));
    }

    @Test
    public void findWorstByScore_WithNullStatistic_ReturnNull() {
        assertNull(findWorstByScore(null));
    }

    @Test
    public void findBest_WithValidData_ReturnBest() {
        TestStatistic best = findBest(getTestStatistics(), TestStatistic::getTotalFrags);
        assertNotNull(best);
        assertThat(best, TestStatisticMatcher.matcher(getBestScoreTestStatistic()));
    }

    @Test
    public void findBest_WithNullStatistic_ReturnNull() {
        assertNull(findBest(null, TestStatistic::getTotalFrags));
    }

    @Test
    public void findBest_WithNullToIntExtractor_ReturnNull() {
        assertNull(findBest(getTestStatistics(), null));
    }

    @Test
    public void findWorst_WithValidData_ReturnWorst() {
        TestStatistic worst = findWorst(getTestStatistics(), AbstractStatistic::getTotalDamageDealt);
        assertNotNull(worst);
        assertThat(worst, TestStatisticMatcher.matcher(getBestScoreTestStatistic()));
    }

    @Test
    public void findWorst_WithNullStatistic_ReturnNull() {
        assertNull(findWorst(null, AbstractStatistic::getTotalScore));
    }

    @Test
    public void findWorst_WithNullToIntExtractor_ReturnNull() {
        assertNull(findWorst(getTestStatistics(), null));
    }

    private TestStatistic getBestScoreTestStatistic() {
        int damage = 2500;
        int frags = 50;
        return new TestStatistic(damage, frags, (damage + frags * 300));
    }

    private TestStatistic getWorstScoreTestStatistic() {
        int damage = 3000;
        int frags = 1;
        return new TestStatistic(damage, frags, (damage + frags * 300));
    }

    private List<TestStatistic> getTestStatistics() {
        return Arrays.asList(getBestScoreTestStatistic(), getWorstScoreTestStatistic());
    }

}