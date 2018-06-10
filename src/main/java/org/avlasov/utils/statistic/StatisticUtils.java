package org.avlasov.utils.statistic;

import org.avlasov.entity.statistic.AbstractStatistic;

import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * Created By artemvlasov on 29/05/2018
 **/
public class StatisticUtils {

    public static <T extends AbstractStatistic> T findBestByScore(List<T> statisticData) {
        if (statisticData != null) {
            return findBest(statisticData, AbstractStatistic::getTotalScore);
        }
        return null;
    }

    public static <T extends AbstractStatistic> T findWorstByScore(List<T> statisticData) {
        if (statisticData != null) {
            return findWorst(statisticData, AbstractStatistic::getTotalScore);
        }
        return null;
    }

    public static <T extends AbstractStatistic> T findBest(List<T> statisticData, ToIntFunction<T> comparatorKeyExtractor) {
        if (statisticData != null && comparatorKeyExtractor != null) {
            return statisticData.parallelStream()
                    .max(Comparator.comparingInt(comparatorKeyExtractor))
                    .orElse(null);
        }
        return null;
    }

    public static <T extends AbstractStatistic> T findWorst(List<T> statisticData, ToIntFunction<T> comparatorKeyExtractor) {
        if (statisticData != null && comparatorKeyExtractor != null) {
            return statisticData.parallelStream()
                    .min(Comparator.comparingInt(comparatorKeyExtractor))
                    .orElse(null);
        }
        return null;
    }

}
