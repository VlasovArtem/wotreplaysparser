package org.avlasov.utils.statistic;

import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.PlayerMatch;
import org.avlasov.entity.statistic.MatchStatistic;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created By artemvlasov on 01/06/2018
 **/
@Component
public class MatchStatisticUtils extends AbstractStatisticUtils<Optional<MatchStatistic>> {

    @Override
    public Optional<MatchStatistic> calculateStatistic(List<Match> matches) {
        if (matches != null && !matches.isEmpty()) {
            return Optional.of(MatchStatistic.builder()
                    .top10PlatoonMaxDamageDealtMatches(calculateTop10PlatoonMaxDamageDealtMatches(matches))
                    .top10PlatoonMaxFragsMatches(calculateTop10PlatoonMaxFragsMatches(matches))
                    .top10PlatoonMaxScoreMatches(calculateTop10PlatoonMaxScoreMatches(matches))
                    .top10FragsPlayerMatch(calculateTop10FragsPlayerMatch(matches))
                    .top10DamageDealtPlayerMatch(calculateTop10DamageDealtPlayerMatch(matches))
                    .build());
        }
        return Optional.empty();
    }

    private List<PlayerMatch> calculateTop10DamageDealtPlayerMatch(List<Match> matches) {
        return matches.parallelStream()
                .flatMap(match -> match.getPlayerMatches().stream())
                .sorted(Comparator.comparingInt(PlayerMatch::getDamage).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<PlayerMatch> calculateTop10FragsPlayerMatch(List<Match> matches) {
        return matches.parallelStream()
                .flatMap(match -> match.getPlayerMatches().stream())
                .sorted(Comparator.comparingInt(PlayerMatch::getFrags).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }

    private List<Match> calculateTop10PlatoonMaxScoreMatches(List<Match> matches) {
        return calculateTopNPlatoonMaxData(matches,
                Comparator.comparingInt(match -> match.getResult().getMatchPlatoonFrags()),
                10,
                true);
    }

    private List<Match> calculateTop10PlatoonMaxFragsMatches(List<Match> matches) {
        return calculateTopNPlatoonMaxData(matches,
                Comparator.comparingInt(match -> match.getResult().getMatchPlatoonFrags()),
                10,
                true);
    }

    private List<Match> calculateTop10PlatoonMaxDamageDealtMatches(List<Match> matches) {
        return calculateTopNPlatoonMaxData(matches,
                Comparator.comparingInt(match -> match.getResult().getMatchPlatoonDamageDealt()),
                10,
                true);
    }

    private static <T> List<T> calculateTopNPlatoonMaxData(List<T> matches, Comparator<T> matchComparator, int nResults, boolean reversed) {
        matchComparator = reversed ? matchComparator.reversed() : matchComparator;
        return matches
                .parallelStream()
                .sorted(matchComparator)
                .limit(nResults)
                .collect(Collectors.toList());
    }

}
