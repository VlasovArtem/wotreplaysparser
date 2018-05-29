package org.avlasov.utils;

import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.entity.statistic.AbstractStatistic;
import org.avlasov.entity.statistic.PlayerStatistic;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created By artemvlasov on 29/05/2018
 **/
public class DrawGroupPlayerStatisticUtils {

    public static Map<DrawGroup, List<PlayerStatistic>> collectDrawGroupPlayers(List<PlayerStatistic> playerStatistics) {
        Map<DrawGroup, List<PlayerStatistic>> data = new HashMap<>();
        for (PlayerStatistic playerStatistic : playerStatistics) {
            data.compute(playerStatistic.getPlayer().getDrawGroup(), (drawGroup, playerStatistics1) -> {
               if (playerStatistics1 == null)
                   playerStatistics1 = new ArrayList<>();
               playerStatistics1.add(playerStatistic);
               return playerStatistics1;
            });
        }
        for (List<PlayerStatistic> statistics : data.values()) {
            statistics.sort(Comparator.comparingInt(AbstractStatistic::getTotalScore));
        }
        return data;
    }

    public static Map<DrawGroup, PlayerStatistic> findWorstDrawGroupPlayer(Map<DrawGroup, List<PlayerStatistic>> data) {
        return findDrawGroupPlayer(data, o ->  StatisticUtils.findBestByScore(o.getValue()));
    }

    public static Map<DrawGroup, PlayerStatistic> findBestDrawGroupPlayer(Map<DrawGroup, List<PlayerStatistic>> data) {
        return findDrawGroupPlayer(data, o ->  StatisticUtils.findWorstByScore(o.getValue()));
    }

    private static Map<DrawGroup, PlayerStatistic> findDrawGroupPlayer(Map<DrawGroup, List<PlayerStatistic>> data, Function<Map.Entry<DrawGroup, List<PlayerStatistic>>, PlayerStatistic> valueExtractor) {
        return data.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, valueExtractor));
    }

}
