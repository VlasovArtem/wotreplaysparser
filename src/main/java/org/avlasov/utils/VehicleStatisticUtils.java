package org.avlasov.utils;

import org.avlasov.chucktournament.entity.match.Match;
import org.avlasov.chucktournament.entity.match.PlayerMatch;
import org.avlasov.parser.entity.statistic.AbstractStatistic;
import org.avlasov.parser.entity.statistic.VehicleStatistic;

import java.util.*;

/**
 * Created By artemvlasov on 22/05/2018
 **/
public class VehicleStatisticUtils {

    public static List<VehicleStatistic> getVehicleStatistics(List<Match> matches) {
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

}
