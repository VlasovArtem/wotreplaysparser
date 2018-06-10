package org.avlasov.utils.statistic;

import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.entity.statistic.AbstractStatistic;
import org.avlasov.entity.statistic.DrawGroupPlayerStatistic;
import org.avlasov.entity.statistic.PlayerStatistic;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created By artemvlasov on 29/05/2018
 **/
@Component
public class DrawGroupPlayerStatisticUtils {

    public Map<DrawGroup, List<DrawGroupPlayerStatistic>> collectDrawGroupPlayers(List<PlayerStatistic> playerStatistics) {
        Map<DrawGroup, List<DrawGroupPlayerStatistic>> data = new HashMap<>();
        for (PlayerStatistic playerStatistic : playerStatistics) {
            data.compute(playerStatistic.getPlayer().getDrawGroup(), (drawGroup, playerStatistics1) -> {
               if (playerStatistics1 == null)
                   playerStatistics1 = new ArrayList<>();
               playerStatistics1.add(new DrawGroupPlayerStatistic(playerStatistic));
               return playerStatistics1;
            });
        }
        for (List<DrawGroupPlayerStatistic> statistics : data.values()) {
            statistics.sort(Comparator.comparingInt(AbstractStatistic::getTotalScore).reversed());
        }
        return data;
    }

    public Map<DrawGroup, DrawGroupPlayerStatistic> findWorstDrawGroupPlayer(Map<DrawGroup, List<DrawGroupPlayerStatistic>> data) {
        return findDrawGroupPlayer(data, o ->  StatisticUtils.findWorstByScore(o.getValue()));
    }

    public Map<DrawGroup, DrawGroupPlayerStatistic> findBestDrawGroupPlayer(Map<DrawGroup, List<DrawGroupPlayerStatistic>> data) {
        return findDrawGroupPlayer(data, o ->  StatisticUtils.findBestByScore(o.getValue()));
    }

    private Map<DrawGroup, DrawGroupPlayerStatistic> findDrawGroupPlayer(Map<DrawGroup, List<DrawGroupPlayerStatistic>> data, Function<Map.Entry<DrawGroup, List<DrawGroupPlayerStatistic>>, DrawGroupPlayerStatistic> valueExtractor) {
        return data.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, valueExtractor));
    }

}
