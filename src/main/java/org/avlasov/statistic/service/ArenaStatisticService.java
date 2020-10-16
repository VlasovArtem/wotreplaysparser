package org.avlasov.statistic.service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.avlasov.parser.replay.entity.Replay;
import org.avlasov.parser.replay.entity.match.MatchInternalVehiclePlayerId;
import org.avlasov.parser.replay.entity.match.MatchVehicleDetail;
import org.avlasov.statistic.config.properties.StatisticProperties;
import org.avlasov.statistic.entity.AbstractArenasStatistic;
import org.avlasov.statistic.entity.ArenasStatisticByVehicle;
import org.avlasov.statistic.entity.ArenasStatisticByVehicleType;
import org.avlasov.statistic.entity.data.ReplayStatisticArenaInfo;
import org.avlasov.statistic.entity.data.StatisticArena;
import org.avlasov.statistic.entity.data.StatisticArenaInfo;
import org.avlasov.statistic.entity.data.StatisticVehicle;
import org.avlasov.wotapi.entity.arena.Arena;
import org.avlasov.wotapi.entity.vehicle.Vehicle;
import org.avlasov.wotapi.enums.VehicleType;
import org.avlasov.wotapi.service.WotApiDataService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.avlasov.statistic.entity.data.StatisticVehicle.TO_STATISTIC_ENTITY;

@Component
@Slf4j
public class ArenaStatisticService {

    private final List<Arena> arenas;
    private final List<Vehicle> vehicles;
    private final File vehicleArenaStatisticFile;
    private final File vehicleTypeArenaStatisticFile;
    private final ObjectMapper objectMapper;

    public ArenaStatisticService(WotApiDataService wotApiDataService,
                                 StatisticProperties statisticProperties,
                                 @Qualifier("wotReplayParserObjectMapper") ObjectMapper objectMapper) {
        arenas = wotApiDataService.getArenas();
        vehicles = wotApiDataService.getVehicles();
        this.objectMapper = objectMapper;
        vehicleArenaStatisticFile = new File(statisticProperties.getVehicleArenaStatisticFilename());
        vehicleTypeArenaStatisticFile = new File(statisticProperties.getVehicleTypeArenaStatisticFilename());
    }

    public <T extends AbstractArenasStatistic> List<T> readArenasStatistic(Class<T> tClass) {
        if (tClass.equals(ArenasStatisticByVehicleType.class)) {
            return readData(vehicleTypeArenaStatisticFile, tClass);
        } else if (tClass.equals(ArenasStatisticByVehicle.class)) {
            return readData(vehicleArenaStatisticFile, tClass);
        }
        return Collections.emptyList();
    }

    public <T extends AbstractArenasStatistic> void writeListArenasStatistic(List<T> arenasStatisticByVehicleTypes, Class<T> tClass) {
        if (tClass.equals(ArenasStatisticByVehicleType.class)) {
            writeData(vehicleTypeArenaStatisticFile, tClass);
        } else if (tClass.equals(ArenasStatisticByVehicle.class)) {
            writeData(vehicleArenaStatisticFile, tClass);
        }
        writeData(vehicleTypeArenaStatisticFile, arenasStatisticByVehicleTypes);
    }

    public <T extends AbstractArenasStatistic> List<T> limitData(List<T> data, Comparator<StatisticArenaInfo> comparator,
                                                                 int limit, Class<T> tClass) {
        return data.stream()
                .map(mapArenasStatistic(comparator, limit, tClass))
                .collect(toList());
    }

    private <T extends AbstractArenasStatistic> Function<T, T> mapArenasStatistic(Comparator<StatisticArenaInfo> comparator,
                                                                                  int limit, Class<T> tClass) {
        return t -> {
            try {
                T object = tClass.newInstance();
                List<StatisticArenaInfo> arenaInfoSet = t.getStatisticArenaInfos()
                        .stream()
                        .sorted(comparator)
                        .limit(limit)
                        .collect(Collectors.toList());
                if (tClass.equals(ArenasStatisticByVehicleType.class)) {
                    ArenasStatisticByVehicleType type = (ArenasStatisticByVehicleType) object;
                    type.setVehicleType(((ArenasStatisticByVehicleType) t).getVehicleType());
                } else if (tClass.equals(ArenasStatisticByVehicle.class)) {
                    ArenasStatisticByVehicle vehicle = (ArenasStatisticByVehicle) object;
                    vehicle.setStatisticVehicle(((ArenasStatisticByVehicle) t).getStatisticVehicle());
                }
                object.setStatisticArenaInfos(arenaInfoSet);
                return object;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    public List<ArenasStatisticByVehicleType> collectArenasStatisticByVehicleTypes(Set<Replay> replays) {
        Map<VehicleType, Map<StatisticArena, List<ReplayStatisticArenaInfo>>> data = new EnumMap<>(VehicleType.class);

        for (Replay replay : replays) {
            StatisticArena statisticArena = StatisticArena.TO_STATISTIC_ENTITY.getDestination(findReplaysArena(replay));
            Map<VehicleType, List<MatchVehicleDetail>> vehicleTypeToMatchVehicles = getVehicleTypeToMatchVehicles(replay);
            Map<VehicleType, ReplayStatisticArenaInfo> arenaInfoMap = getReplayStatisticArenaInfoMap(vehicleTypeToMatchVehicles, replay);
            updateData(data, statisticArena, arenaInfoMap);
        }
        return data.entrySet()
                .stream()
                .map(mapToArenasStatisticByVehicleType())
                .collect(Collectors.toList());
    }

    private Map<VehicleType, List<MatchVehicleDetail>> getVehicleTypeToMatchVehicles(Replay replay) {
        return replay.getMatchResult().getVehicles()
                .entrySet()
                .stream()
                .map(entry -> Pair.of(findVehicleType(entry.getKey(), replay), entry.getValue().get(0)))
                .collect(groupingBy(Pair::getKey, mapping(Pair::getValue, toList())));
    }

    public List<ArenasStatisticByVehicle> collectArenasStatisticByVehicles(Set<Replay> replays) {
        Map<StatisticVehicle, Map<StatisticArena, List<ReplayStatisticArenaInfo>>> data = new HashMap<>();

        for (Replay replay : replays) {
            StatisticArena statisticArena = StatisticArena.TO_STATISTIC_ENTITY.getDestination(findReplaysArena(replay));
            Map<StatisticVehicle, List<MatchVehicleDetail>> statisticVehicleToMatchVehicles = getStatisticVehicleToMatchVehicles(replay);
            Map<StatisticVehicle, ReplayStatisticArenaInfo> statisticVehicleData =
                    getReplayStatisticArenaInfoMap(statisticVehicleToMatchVehicles, replay);
            updateData(data, statisticArena, statisticVehicleData);
        }
        return data.entrySet()
                .stream()
                .map(mapToArenasStatisticByVehicle())
                .collect(Collectors.toList());
    }

    private Map<StatisticVehicle, List<MatchVehicleDetail>> getStatisticVehicleToMatchVehicles(Replay replay) {
        return replay.getMatchResult().getVehicles()
                .entrySet()
                .stream()
                .map(entry -> Pair.of(TO_STATISTIC_ENTITY.getDestination(findVehicle(entry.getKey(), replay)), entry.getValue().get(0)))
                .collect(groupingBy(Pair::getKey, mapping(Pair::getValue, toList())));
    }

    private <T> void updateData(
            Map<T, Map<StatisticArena, List<ReplayStatisticArenaInfo>>> data,
            StatisticArena statisticArena,
            Map<T, ReplayStatisticArenaInfo> replayData) {
        for (Map.Entry<T, ReplayStatisticArenaInfo> entry : replayData.entrySet()) {
            Map<StatisticArena, List<ReplayStatisticArenaInfo>> arenaData = data.get(entry.getKey());
            if (arenaData == null) {
                arenaData = new HashMap<>();
                List<ReplayStatisticArenaInfo> replayStatisticArenaInfos = new ArrayList<>();
                replayStatisticArenaInfos.add(entry.getValue());
                arenaData.put(statisticArena, replayStatisticArenaInfos);
                data.put(entry.getKey(), arenaData);
            } else {
                arenaData.compute(statisticArena, (statisticArena1, replayStatisticArenaInfos) -> {
                    if (replayStatisticArenaInfos == null) {
                        replayStatisticArenaInfos = new ArrayList<>();
                    }
                    replayStatisticArenaInfos.add(entry.getValue());
                    return replayStatisticArenaInfos;
                });
            }
        }
    }

    private Function<Map.Entry<StatisticVehicle, Map<StatisticArena, List<ReplayStatisticArenaInfo>>>, ArenasStatisticByVehicle> mapToArenasStatisticByVehicle() {
        return entry -> {
            ArenasStatisticByVehicle arenasStatisticByVehicle = new ArenasStatisticByVehicle();
            arenasStatisticByVehicle.setStatisticVehicle(entry.getKey());
            arenasStatisticByVehicle.setStatisticArenaInfos(getStatisticArenaInfos(entry));
            return arenasStatisticByVehicle;
        };
    }

    private Function<Map.Entry<VehicleType, Map<StatisticArena, List<ReplayStatisticArenaInfo>>>, ArenasStatisticByVehicleType> mapToArenasStatisticByVehicleType() {
        return entry -> {
            ArenasStatisticByVehicleType arenasStatisticByVehicleType = new ArenasStatisticByVehicleType();
            arenasStatisticByVehicleType.setVehicleType(entry.getKey());
            arenasStatisticByVehicleType.setStatisticArenaInfos(getStatisticArenaInfos(entry));
            return arenasStatisticByVehicleType;
        };
    }

    private <T> List<StatisticArenaInfo> getStatisticArenaInfos(Map.Entry<T, Map<StatisticArena, List<ReplayStatisticArenaInfo>>> entry) {
        List<StatisticArenaInfo> statisticArenaInfos = new ArrayList<>();
        for (Map.Entry<StatisticArena, List<ReplayStatisticArenaInfo>> statisticArenaListEntry : entry.getValue().entrySet()) {
            ReplayStatisticArenaInfo totalReplaysStatisticArenaInfo = statisticArenaListEntry.getValue()
                    .stream()
                    .reduce(sumInfos())
                    .orElseGet(() -> {
                        log.warn("Statistic is not collected for arena with id " + statisticArenaListEntry.getKey().getArenaId());
                        return null;
                    });
            if (totalReplaysStatisticArenaInfo != null) {
                statisticArenaInfos.add(getStatisticArenaInfo(statisticArenaListEntry.getKey(), totalReplaysStatisticArenaInfo));
            }
        }
        statisticArenaInfos.sort(StatisticArenaInfo::compareTo);
        return statisticArenaInfos;
    }

    private StatisticArenaInfo getStatisticArenaInfo(StatisticArena statisticArena, ReplayStatisticArenaInfo replayStatisticArenaInfo) {
        StatisticArenaInfo statisticArenaInfo = new StatisticArenaInfo();
        statisticArenaInfo.setStatisticArena(statisticArena);
        statisticArenaInfo.setBattles(replayStatisticArenaInfo.getBattles());
        statisticArenaInfo.setLose(replayStatisticArenaInfo.getLose());
        statisticArenaInfo.setWins(replayStatisticArenaInfo.getWins());
        statisticArenaInfo.setFragAverage((double) replayStatisticArenaInfo.getFrags() / replayStatisticArenaInfo.getBattles());
        statisticArenaInfo.setDamageDealtAverage((double) replayStatisticArenaInfo.getDamageDealt() / replayStatisticArenaInfo.getBattles());
        statisticArenaInfo.setXpAverage((double) replayStatisticArenaInfo.getXp() / replayStatisticArenaInfo.getBattles());
        statisticArenaInfo.setSpottedAverage((double) replayStatisticArenaInfo.getSpotted() / replayStatisticArenaInfo.getBattles());
        statisticArenaInfo.setDamageAssistedAverage((double) replayStatisticArenaInfo.getDamageAssisted() / replayStatisticArenaInfo.getBattles());
        statisticArenaInfo.setWinPercentage((double) replayStatisticArenaInfo.getWins() / statisticArenaInfo.getBattles());
        statisticArenaInfo.setDroppedCapturePointsAverage((double) replayStatisticArenaInfo.getDroppedCapturePoints()
                / statisticArenaInfo.getBattles());
        statisticArenaInfo.setEfficiency(calculateEfficiency(statisticArenaInfo));
        return statisticArenaInfo;
    }

    private <T> Map<T, ReplayStatisticArenaInfo> getReplayStatisticArenaInfoMap(
            Map<T, List<MatchVehicleDetail>> map, Replay replay) {
        return map
                .entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey, getReplayStatisticArenaInfo(replay)));
    }

    private <T> Function<Map.Entry<T, List<MatchVehicleDetail>>, ReplayStatisticArenaInfo> getReplayStatisticArenaInfo(Replay replay) {
        return entry -> {
            int winnerTeam = replay.getMatchResult().getCommon().getWinnerTeam();
            ReplayStatisticArenaInfo replayStatisticArenaInfo = new ReplayStatisticArenaInfo();
            Map<Integer, List<MatchVehicleDetail>> collect = entry.getValue()
                    .stream()
                    .collect(groupingBy(MatchVehicleDetail::getTeam));
            setBattleInfo(collect, 1, winnerTeam, replayStatisticArenaInfo);
            setBattleInfo(collect, 2, winnerTeam, replayStatisticArenaInfo);
            return replayStatisticArenaInfo;
        };
    }

    private Arena findReplaysArena(Replay replay) {
        String mapName = replay.getMatchPlayerDetails().getMapName();
        return arenas.stream()
                .filter(arena -> arena.getArenaId().equals(mapName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Map with name is not found " + mapName));
    }

    private Vehicle findVehicle(MatchInternalVehiclePlayerId arenaInternalVehicleId, Replay replay) {
        String vehicleType = replay.getMatchPlayerDetails().getVehicles().get(arenaInternalVehicleId)
                .getVehicleType();
        return vehicles.stream()
                .filter(vehicle -> vehicleType.contains(vehicle.getTag()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Vehicle with type " + vehicleType + " is not found"));
    }

    private VehicleType findVehicleType(MatchInternalVehiclePlayerId arenaInternalVehicleId, Replay replay) {
        String vehicleType = replay.getMatchPlayerDetails().getVehicles().get(arenaInternalVehicleId)
                .getVehicleType();
        return vehicles.stream()
                .filter(vehicle -> vehicleType.contains(vehicle.getTag()))
                .map(Vehicle::getType)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Vehicle with type " + vehicleType + " is not found"));
    }

    private void setBattleInfo(Map<Integer, List<MatchVehicleDetail>> data, int team, int winnerTeam,
                               ReplayStatisticArenaInfo replayStatisticArenaInfo) {
        List<MatchVehicleDetail> matchVehicles = data.get(team);
        if (matchVehicles != null) {
            for (MatchVehicleDetail matchVehicle : matchVehicles) {
                replayStatisticArenaInfo.setBattles(replayStatisticArenaInfo.getBattles() + 1);
                if (winnerTeam == team) {
                    replayStatisticArenaInfo.setWins(replayStatisticArenaInfo.getWins() + 1);
                } else {
                    replayStatisticArenaInfo.setLose(replayStatisticArenaInfo.getLose() + 1);
                }
                replayStatisticArenaInfo.setDroppedCapturePoints(replayStatisticArenaInfo.getDroppedCapturePoints()
                        + matchVehicle.getDroppedCapturePoints());
                replayStatisticArenaInfo.setDamageDealt(replayStatisticArenaInfo.getDamageDealt() + matchVehicle.getDamageDealt());
                replayStatisticArenaInfo.setFrags(replayStatisticArenaInfo.getFrags() + matchVehicle.getKills());
                replayStatisticArenaInfo.setXp(replayStatisticArenaInfo.getXp() + matchVehicle.getXp());
                replayStatisticArenaInfo.setSpotted(replayStatisticArenaInfo.getSpotted() + matchVehicle.getSpotted());
                replayStatisticArenaInfo.setDamageAssisted(replayStatisticArenaInfo.getDamageAssisted()
                        + matchVehicle.getDamageAssistedRadio()
                        + matchVehicle.getDamageAssistedStun()
                        + matchVehicle.getDamageAssistedTrack());
            }
        }
    }

    private BinaryOperator<ReplayStatisticArenaInfo> sumInfos() {
        return (first, second) -> {
            sumInt(ReplayStatisticArenaInfo::setBattles, ReplayStatisticArenaInfo::getBattles)
                    .andThen(sumInt(ReplayStatisticArenaInfo::setWins, ReplayStatisticArenaInfo::getWins))
                    .andThen(sumInt(ReplayStatisticArenaInfo::setLose, ReplayStatisticArenaInfo::getLose))
                    .andThen(sumInt(ReplayStatisticArenaInfo::setDamageDealt, ReplayStatisticArenaInfo::getDamageDealt))
                    .andThen(sumInt(ReplayStatisticArenaInfo::setXp, ReplayStatisticArenaInfo::getXp))
                    .andThen(sumInt(ReplayStatisticArenaInfo::setFrags, ReplayStatisticArenaInfo::getFrags))
                    .andThen(sumInt(ReplayStatisticArenaInfo::setSpotted, ReplayStatisticArenaInfo::getSpotted))
                    .andThen(sumInt(ReplayStatisticArenaInfo::setDamageAssisted, ReplayStatisticArenaInfo::getDamageAssisted))
                    .andThen(sumInt(ReplayStatisticArenaInfo::setDroppedCapturePoints, ReplayStatisticArenaInfo::getDroppedCapturePoints))
                    .accept(first, second);
            return first;
        };
    }

    private BiConsumer<ReplayStatisticArenaInfo, ReplayStatisticArenaInfo> sumInt(BiConsumer<ReplayStatisticArenaInfo, Integer> setter,
                                                                                  Function<ReplayStatisticArenaInfo, Integer> getter) {
        return (first, second) -> setter.accept(first, getter.apply(first) + getter.apply(second));
    }

    private double calculateEfficiency(StatisticArenaInfo statisticArenaInfo) {

//        rWINc    = max(0, (rWIN    - 0.71) / (1 - 0.71) )
//        rDAMAGEc = max(0, (rDAMAGE - 0.22) / (1 - 0.22) )
//        rFRAGc   = max(0, min(rDAMAGEc + 0.2, (rFRAG   - 0.12) / (1 - 0.12)))
//        rSPOTc   = max(0, min(rDAMAGEc + 0.1, (rSPOT   - 0.38) / (1 - 0.38)))
//        rDEFc    = max(0, min(rDAMAGEc + 0.1, (rDEF    - 0.10) / (1 - 0.10)))

//        WN8 = 980*rDAMAGEc + 210*rDAMAGEc*rFRAGc + 155*rFRAGc*rSPOTc + 75*rDEFc*rFRAGc + 145*MIN(1.8,rWINc)

//        double rWin = Math.max(0, (statisticArenaInfo.getWinPercentage() - 0.71) / (1 - 0.71));
//        double rDamage = Math.max(0, (statisticArenaInfo.getDamageDealtAverage() - 0.22) / (1 - 0.22));
//        double rFrags = Math.max(0, Math.min(rDamage + 0.2, (statisticArenaInfo.getFragAverage() - 0.12) / (1 - 0.12)));
//        double rSpot = Math.max(0, Math.min(rDamage + 0.1, (statisticArenaInfo.getSpottedAverage() - 0.38) / (1 - 0.38)));
//        double rDef = Math.max(0, Math.min(rDamage + 0.1, (statisticArenaInfo.getDroppedCapturePointsAverage() - 0.12) / (1 - 0.12)));
//
//        return 980 * rDamage + 210 * rDamage * rFrags + 155 * rFrags * rSpot + 75 * rDef * rFrags + 145 * (Math.min(1.8, rWin));
        return statisticArenaInfo.getXpAverage();
    }

    public void writeData(File file, Object data) {
        try {
            objectMapper.writeValue(file, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> List<T> readData(File file, Class<T> tClass) {
        return readData(file, objectMapper.getTypeFactory().constructCollectionLikeType(List.class, tClass));
    }

    private <T> T readData(File file, JavaType javaType) {
        if (!file.exists()) {
            return null;
        }
        try {
            return objectMapper.readValue(file, javaType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
