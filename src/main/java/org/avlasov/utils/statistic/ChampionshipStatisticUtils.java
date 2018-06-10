package org.avlasov.utils.statistic;

import lombok.Builder;
import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.enums.DrawGroup;
import org.avlasov.entity.statistic.*;
import org.avlasov.utils.MatchUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.avlasov.utils.statistic.StatisticUtils.findBestByScore;
import static org.avlasov.utils.statistic.StatisticUtils.findWorstByScore;

/**
 * Created By artemvlasov on 09/06/2018
 **/
@Component
public class ChampionshipStatisticUtils extends AbstractStatisticUtils<Optional<ChampionshipStatistic>> {

    private final PlatoonStatisticUtils platoonStatisticUtils;
    private final VehicleStatisticUtils vehicleStatisticUtils;
    private final PlayerStatisticUtils playerStatisticUtils;
    private final MatchStatisticUtils matchStatisticUtils;
    private final MatchesStatisticUtils matchesStatisticUtils;
    private final MatchUtils matchUtils;
    private final DrawGroupPlayerStatisticUtils drawGroupPlayerStatisticUtils;

    @Builder
    public ChampionshipStatisticUtils(PlatoonStatisticUtils platoonStatisticUtils, VehicleStatisticUtils vehicleStatisticUtils, PlayerStatisticUtils playerStatisticUtils, MatchStatisticUtils matchStatisticUtils, MatchesStatisticUtils matchesStatisticUtils, MatchUtils matchUtils, DrawGroupPlayerStatisticUtils drawGroupPlayerStatisticUtils) {
        this.platoonStatisticUtils = Objects.requireNonNull(platoonStatisticUtils);
        this.vehicleStatisticUtils = Objects.requireNonNull(vehicleStatisticUtils);
        this.playerStatisticUtils = Objects.requireNonNull(playerStatisticUtils);
        this.matchStatisticUtils = Objects.requireNonNull(matchStatisticUtils);
        this.matchesStatisticUtils = Objects.requireNonNull(matchesStatisticUtils);
        this.matchUtils = Objects.requireNonNull(matchUtils);
        this.drawGroupPlayerStatisticUtils = Objects.requireNonNull(drawGroupPlayerStatisticUtils);
    }

    @Override
    public Optional<ChampionshipStatistic> calculateStatistic(List<Match> matches) {
        if (matches !=  null && !matches.isEmpty()) {
            List<VehicleStatistic> vehicleStatistics = vehicleStatisticUtils.calculateStatistic(matches);
            List<PlatoonStatistic> platoonStatistics = platoonStatisticUtils.calculateStatistic(matches);
            List<PlayerStatistic> playerStatistics = playerStatisticUtils.calculateStatistic(matches);
            MatchStatistic matchStatistic = matchStatisticUtils.calculateStatistic(matches).orElse(null);
            Map<DrawGroup, List<DrawGroupPlayerStatistic>> drawGroupListMap = drawGroupPlayerStatisticUtils.collectDrawGroupPlayers(playerStatistics);
            return calculateStatistic(matches, vehicleStatistics, platoonStatistics, playerStatistics, drawGroupListMap, matchStatistic);
        }
        return Optional.empty();
    }

    public Optional<ChampionshipStatistic> calculateStatistic(List<Match> matches,
                                                                 List<VehicleStatistic> vehicleStatistics,
                                                                 List<PlatoonStatistic> platoonStatistics,
                                                                 List<PlayerStatistic> playerStatistics,
                                                                 Map<DrawGroup, List<DrawGroupPlayerStatistic>> drawGroupListMap,
                                                                 MatchStatistic matchStatistic) {
        return Optional.ofNullable(ChampionshipStatistic.builder()
                .matchesStatistic(matchesStatisticUtils.calculateStatistic(matches).orElse(null))
                .bestMatch(matchUtils.findBestMatch(matches).orElse(null))
                .worstMatch(matchUtils.findWorstMatch(matches).orElse(null))
                .bestPlatoon(findBestByScore(platoonStatistics))
                .worstPlatoon(findWorstByScore(platoonStatistics))
                .bestDrawGroupsPlayer(drawGroupPlayerStatisticUtils.findBestDrawGroupPlayer(drawGroupListMap))
                .worstDrawGroupsPlayers(drawGroupPlayerStatisticUtils.findWorstDrawGroupPlayer(drawGroupListMap))
                .bestPlayer(findBestByScore(playerStatistics))
                .worstPlayer(findWorstByScore(playerStatistics))
                .bestVehicle(findBestByScore(vehicleStatistics))
                .matchStatistic(matchStatistic)
                .build());
    }

}
