package org.avlasov.utils;

import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.enums.Result;
import org.avlasov.entity.statistic.MatchesStatistic;

import java.util.Comparator;
import java.util.List;

/**
 * Created By artemvlasov on 22/05/2018
 **/
public class MatchStatisticUtils {

    public static MatchesStatistic calculateMatchesStatistic(List<Match> matches) {
        int totalPlayedMatches = matches.size();
        long totalPlayedTimeInMilliseconds = matches.parallelStream()
                .mapToLong(Match::getMatchDurationInSeconds)
                .sum();
        int totalDamageDealt = matches.parallelStream()
                .mapToInt(match -> match.getResult().getMatchPlatoonDamageDealt())
                .sum();
        int totalFrags = matches.parallelStream()
                .mapToInt(match -> match.getResult().getMatchPlatoonFrags())
                .sum();
        int totalWins = calculateTotalWins(matches);
        int totalScore = calculateTotalScoreGained(matches);
        return MatchesStatistic.builder()
                .totalPlayedMatches(totalPlayedMatches)
                .totalPlayedTimeInSeconds(totalPlayedTimeInMilliseconds)
                .totalDamageDealt(totalDamageDealt)
                .totalFrags(totalFrags)
                .totalWins(totalWins)
                .totalScore(totalScore)
                .build();
    }

    public static Match findBestMatch(List<Match> matches) {
        return matches.parallelStream()
                .max(Comparator.comparingInt(m -> m.getResult().getMatchScore()))
                .get();
    }

    public static Match findWorstMatch(List<Match> matches) {
        return matches.parallelStream()
                .min(Comparator.comparingInt(m -> m.getResult().getMatchScore()))
                .get();
    }

    private static int calculateTotalScoreGained(List<Match> matches) {
        int totalScore = 0;
        totalScore += calculateTotalWins(matches) * 3000;
        totalScore += matches.parallelStream()
                .mapToInt(match -> match.getResult().getMatchPlatoonDamageDealt() + (match.getResult().getMatchPlatoonFrags() * 300))
                .sum();
        return totalScore;
    }

    private static int calculateTotalWins(List<Match> matches) {
        return (int) matches.parallelStream()
                .filter(match -> Result.WIN.equals(match.getResult().getResult()))
                .count();
    }


}
