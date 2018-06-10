package org.avlasov.utils.statistic;

import org.avlasov.entity.match.Match;
import org.avlasov.entity.match.PlayerMatch;
import org.avlasov.entity.statistic.AbstractStatistic;
import org.avlasov.entity.statistic.VehicleStatistic;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created By artemvlasov on 22/05/2018
 **/
@Component
public class VehicleStatisticUtils extends AbstractStatisticUtils<List<VehicleStatistic>> {

    @Override
    public List<VehicleStatistic> calculateStatistic(List<Match> matches) {
        if (matches != null && !matches.isEmpty()) {
            Map<String, List<PlayerMatch>> vehicleMatches = new HashMap<>();
            for (Match match : matches) {
                for (PlayerMatch playerMatch : match.getPlayerMatches()) {
                    vehicleMatches.compute(playerMatch.getVehicleName(), (vehicleName, playerMatches) -> {
                        if (playerMatches == null)
                            playerMatches = new ArrayList<>();
                        playerMatches.add(playerMatch);
                        return playerMatches;
                    });
                }
            }
            List<VehicleStatistic> vehicleStatistics = new ArrayList<>();
            for (Map.Entry<String, List<PlayerMatch>> stringListEntry : vehicleMatches.entrySet()) {
                int totalVehicleDamageDealt = 0;
                int totalVehicleFrags = 0;
                for (PlayerMatch playerMatch : stringListEntry.getValue()) {
                    totalVehicleDamageDealt += playerMatch.getDamage();
                    totalVehicleFrags += playerMatch.getFrags();
                }
                vehicleStatistics.add(
                        VehicleStatistic.builder()
                                .vehicleName(stringListEntry.getKey())
                                .numberOfMatches(stringListEntry.getValue().size())
                                .totalDamageDealt(totalVehicleDamageDealt)
                                .totalFrags(totalVehicleFrags)
                                .totalScore(totalVehicleDamageDealt + (totalVehicleFrags * 300))
                                .build());
            }
            vehicleStatistics.sort(Comparator.comparing(AbstractStatistic::getTotalScore).reversed());
            return vehicleStatistics;
        }
        return Collections.emptyList();
    }

}
