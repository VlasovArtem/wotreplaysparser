package org.avlasov.entity.statistic;

import lombok.Builder;
import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.enums.DrawGroup;

import java.util.Map;

/**
 * Created By artemvlasov on 22/05/2018
 **/
@Builder
public class ChampionshipStatistic {

    private MatchesStatistic matchesStatistic;
    private Match bestMatch;
    private Match worstMatch;
    private Map<DrawGroup, DrawGroupPlayerStatistic> bestDrawGroupsPlayer;
    private Map<DrawGroup, DrawGroupPlayerStatistic> worstDrawGroupsPlayers;
    private PlatoonStatistic bestPlatoon;
    private PlatoonStatistic worstPlatoon;
    private PlayerStatistic bestPlayer;
    private PlayerStatistic worstPlayer;
    private VehicleStatistic bestVehicle;

}
