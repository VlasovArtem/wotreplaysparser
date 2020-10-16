package org.avlasov.utils;

import org.avlasov.chucktournament.entity.match.enums.DrawGroup;
import org.avlasov.parser.entity.statistic.DrawGroupPlayerStatistic;
import org.avlasov.parser.entity.statistic.PlayerStatistic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created By artemvlasov on 29/05/2018
 **/
public class DrawGroupPlayerStatisticUtils {

    public static Map<DrawGroup, List<DrawGroupPlayerStatistic>> collectDrawGroupPlayers(List<PlayerStatistic> playerStatistics) {
        Map<DrawGroup, List<DrawGroupPlayerStatistic>> data = new HashMap<>();
//        for (PlayerStatistic playerStatistic : playerStatistics) {
//            data.compute(playerStatistic.getPlayer().getDrawGroup(), (drawGroup, playerStatistics1) -> {
//               if (playerStatistics1 == null)
//                   playerStatistics1 = new ArrayList<>();
//               playerStatistics1.add(new DrawGroupPlayerStatistic(playerStatistic));
//               return playerStatistics1;
//            });
//        }
//        for (List<DrawGroupPlayerStatistic> statistics : data.values()) {
//            statistics.sort(Comparator.comparingInt(AbstractStatistic::getTotalScore).reversed());
//        }
        return data;
    }

    public static Map<DrawGroup, DrawGroupPlayerStatistic> findWorstDrawGroupPlayer(Map<DrawGroup, List<DrawGroupPlayerStatistic>> data) {
        return findDrawGroupPlayer(data, o ->  StatisticUtils.findWorstByScore(o.getValue()));
    }

    public static Map<DrawGroup, DrawGroupPlayerStatistic> findBestDrawGroupPlayer(Map<DrawGroup, List<DrawGroupPlayerStatistic>> data) {
        return findDrawGroupPlayer(data, o ->  StatisticUtils.findBestByScore(o.getValue()));
    }

    private static Map<DrawGroup, DrawGroupPlayerStatistic> findDrawGroupPlayer(Map<DrawGroup, List<DrawGroupPlayerStatistic>> data, Function<Map.Entry<DrawGroup, List<DrawGroupPlayerStatistic>>, DrawGroupPlayerStatistic> valueExtractor) {
        return data.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, valueExtractor));
    }

}
