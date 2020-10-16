package org.avlasov.utils;

import org.avlasov.parser.entity.statistic.AbstractStatistic;

import java.util.Comparator;
import java.util.List;
import java.util.function.ToIntFunction;

/**
 * Created By artemvlasov on 29/05/2018
 **/
public class StatisticUtils {

    public static <T extends AbstractStatistic> T findBestByScore(List<T> statisticData) {
        return statisticData.parallelStream()
                .max(Comparator.comparingInt(AbstractStatistic::getTotalScore))
                .orElse(null);
    }

    public static <T extends AbstractStatistic> T findWorstByScore(List<T> statisticData) {
        return statisticData.parallelStream()
                .min(Comparator.comparingInt(AbstractStatistic::getTotalScore))
                .orElse(null);
    }

    public static <T extends AbstractStatistic> T findBest(List<T> statisticData, ToIntFunction<T> comparatorKeyExtractor) {
        return statisticData.parallelStream()
                .max(Comparator.comparingInt(comparatorKeyExtractor))
                .orElse(null);
    }

    public static <T extends AbstractStatistic> T findWorst(List<T> statisticData, ToIntFunction<T> comparatorKeyExtractor) {
        return statisticData.parallelStream()
                .max(Comparator.comparingInt(comparatorKeyExtractor))
                .orElse(null);
    }

}
