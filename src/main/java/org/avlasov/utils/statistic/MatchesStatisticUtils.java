package org.avlasov.utils.statistic;

import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.enums.Result;
import org.avlasov.entity.statistic.MatchesStatistic;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created By artemvlasov on 22/05/2018
 **/
@Component
public class MatchesStatisticUtils extends AbstractStatisticUtils<Optional<MatchesStatistic>> {

    @Override
    public Optional<MatchesStatistic> calculateStatistic(List<Match> matches) {
        if (matches != null && !matches.isEmpty()) {
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
            return Optional.ofNullable(MatchesStatistic.builder()
                    .totalPlayedMatches(totalPlayedMatches)
                    .totalPlayedTimeInSeconds(totalPlayedTimeInMilliseconds)
                    .totalDamageDealt(totalDamageDealt)
                    .totalFrags(totalFrags)
                    .totalWins(totalWins)
                    .totalScore(totalScore)
                    .build());
        }
        return Optional.empty();
    }

    public Match findBestMatch(List<Match> matches) {
        return matches.parallelStream()
                .max(Comparator.comparingInt(m -> m.getResult().getMatchScore()))
                .orElse(null);
    }

    public Match findWorstMatch(List<Match> matches) {
        return matches.parallelStream()
                .min(Comparator.comparingInt(m -> m.getResult().getMatchScore()))
                .orElse(null);
    }

    private int calculateTotalScoreGained(List<Match> matches) {
        int totalScore = 0;
        totalScore += calculateTotalWins(matches) * 3000;
        totalScore += matches.parallelStream()
                .mapToInt(match -> match.getResult().getMatchPlatoonDamageDealt() + (match.getResult().getMatchPlatoonFrags() * 300))
                .sum();
        return totalScore;
    }

    private int calculateTotalWins(List<Match> matches) {
        return (int) matches.parallelStream()
                .filter(match -> Result.WIN.equals(match.getResult().getResult()))
                .count();
    }

}
