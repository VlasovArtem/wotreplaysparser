package org.avlasov.utils;

import org.avlasov.chucktournament.entity.match.Match;
import org.avlasov.chucktournament.entity.match.Player;
import org.avlasov.chucktournament.entity.match.PlayerMatch;
import org.avlasov.parser.entity.statistic.AbstractStatistic;
import org.avlasov.parser.entity.statistic.PlayerStatistic;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created By artemvlasov on 22/05/2018
 **/
public class PlayerStatisticUtils {

    public static List<PlayerStatistic> getPlayerStatistics(List<Match> matches) {
        Map<Player, List<Match>> playerListMap = new HashMap<>();
        for (Match match : matches) {
            for (PlayerMatch playerMatch : match.getPlayerMatches()) {
                playerListMap.compute(playerMatch.getPlayer(), (player, matches1) -> {
                    if (matches1 == null) {
                        matches1 = new ArrayList<>();
                    }
                    matches1.add(match);
                    return matches1;
                });
            }
        }
        List<PlayerStatistic> playerStatistics = new ArrayList<>();
        for (Map.Entry<Player, List<Match>> playerListEntry : playerListMap.entrySet()) {
            List<PlayerMatch> playerMatches = playerListEntry.getValue()
                    .parallelStream()
                    .map(match -> match.getPlayerMatches().parallelStream()
                            .filter(playerMatch -> playerMatch.getPlayer().equals(playerListEntry.getKey()))
                            .findFirst()
                            .get())
                    .collect(Collectors.toList());
            int totalPlayerDamageDealt = playerMatches.stream().mapToInt(PlayerMatch::getDamage).sum();
            int totalPlayerFrags = playerMatches.stream().mapToInt(PlayerMatch::getFrags).sum();
            int totalPlayerScore = totalPlayerDamageDealt + (totalPlayerFrags * 300);
            playerStatistics.add(PlayerStatistic.builder()
                    .matches(playerListEntry.getValue())
                    .platoon(playerListEntry.getValue().get(0).getPlatoon())
                    .player(playerListEntry.getKey())
                    .totalDamageDealt(totalPlayerDamageDealt)
                    .totalFrags(totalPlayerFrags)
                    .totalScore(totalPlayerScore)
                    .build());
        }
        playerStatistics.sort(Comparator.comparing(AbstractStatistic::getTotalScore).reversed());
        return playerStatistics;
    }

}
