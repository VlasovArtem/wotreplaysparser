package org.avlasov.utils;

import org.avlasov.chucktournament.entity.match.Match;
import org.avlasov.chucktournament.entity.match.Platoon;
import org.avlasov.chucktournament.entity.match.Player;
import org.avlasov.chucktournament.entity.match.PlayerMatch;
import org.avlasov.chucktournament.entity.match.enums.Result;
import org.avlasov.parser.entity.statistic.AbstractStatistic;
import org.avlasov.parser.entity.statistic.PlatoonPlayerStatistic;
import org.avlasov.parser.entity.statistic.PlatoonStatistic;

import java.util.*;

/**
 * Created By artemvlasov on 22/05/2018
 **/
public class PlatoonStatisticUtils {

    public static List<PlatoonStatistic> getPlatoonStatistics(List<Match> matches) {
        Map<Platoon, List<Match>> platoonListMap = new HashMap<>();
        for (Match match : matches) {
            platoonListMap.compute(match.getPlatoon(), (platoon, platoonMatches) -> {
                if (platoonMatches == null) {
                    platoonMatches = new ArrayList<>();
                }
                platoonMatches.add(match);
                return platoonMatches;
            });
        }
        List<PlatoonStatistic> platoonStatistics = new ArrayList<>();
        for (Map.Entry<Platoon, List<Match>> platoonListEntry : platoonListMap.entrySet()) {
            int totalPlatoonDamageDealt = calculateTotalPlatoonDamageDealt(platoonListEntry.getValue());
            int totalPlatoonFrags = calculateTotalPlatoonFrags(platoonListEntry.getValue());
            int totalPlatoonScore = calculateTotalPlatoonScore(platoonListEntry.getValue());
            platoonStatistics.add(
                    PlatoonStatistic.builder()
                            .matches(platoonListEntry.getValue())
                            .platoon(platoonListEntry.getKey())
                            .platoonPlayerStatistic(getPlatoonPlayersStatistics(platoonListEntry.getKey(), platoonListEntry.getValue()))
                            .totalDamageDealt(totalPlatoonDamageDealt)
                            .totalFrags(totalPlatoonFrags)
                            .totalScore(totalPlatoonScore)
                            .totalPlatoonPlayedMatches(platoonListEntry.getValue().size())
                            .totalPlatoonMatchesWins(calculateTotalPlatoonWins(matches))
                            .build());
        }
        platoonStatistics.sort(Comparator.comparing(AbstractStatistic::getTotalScore).reversed());
        return platoonStatistics;
    }

    private static List<PlatoonPlayerStatistic> getPlatoonPlayersStatistics(Platoon platoon, List<Match> matches) {
        Map<Player, PlatoonPlayerStatistic> platoonPlayerStatisticMap = new HashMap<>();
        for (Match match : matches) {
            for (PlayerMatch playerMatch : match.getPlayerMatches()) {
                platoonPlayerStatisticMap.compute(playerMatch.getPlayer(), (player, platoonPlayerStatistic) -> {
                    if (platoonPlayerStatistic == null) {
                        platoonPlayerStatistic = new PlatoonPlayerStatistic();
                        platoonPlayerStatistic.setPlatoon(platoon);
                        platoonPlayerStatistic.setPlayer(player);
                    }
                    int score = playerMatch.getDamage() + (playerMatch.getFrags() * 300);
                    platoonPlayerStatistic.setTotalFrags(platoonPlayerStatistic.getTotalFrags() + playerMatch.getFrags());
                    platoonPlayerStatistic.setTotalDamageDealt(platoonPlayerStatistic.getTotalDamageDealt() + playerMatch.getDamage());
                    platoonPlayerStatistic.setTotalScore(platoonPlayerStatistic.getTotalScore() + score);
                    return platoonPlayerStatistic;
                });
            }
        }
        List<PlatoonPlayerStatistic> platoonPlayerStatistics = new ArrayList<>(platoonPlayerStatisticMap.values());
        platoonPlayerStatistics.sort(Comparator.comparing(AbstractStatistic::getTotalScore).reversed());
        return platoonPlayerStatistics;
    }

    private static int calculateTotalPlatoonDamageDealt(List<Match> matches) {
        return matches.parallelStream()
                .mapToInt(match -> match.getResult().getMatchPlatoonDamageDealt())
                .sum();
    }

    private static int calculateTotalPlatoonFrags(List<Match> matches) {
        return matches.parallelStream()
                .mapToInt(match -> match.getResult().getMatchPlatoonFrags())
                .sum();
    }

    private static int calculateTotalPlatoonScore(List<Match> matches) {
        return matches.parallelStream()
                .mapToInt(match -> match.getResult().getMatchScore())
                .sum();
    }

    private static int calculateTotalPlatoonWins(List<Match> matches) {
        return (int) matches.parallelStream()
                .filter(match -> Result.WIN.equals(match.getResult().getResult()))
                .count();
    }

}
