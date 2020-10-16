package org.avlasov.parser.replay.entity.match;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Data
@EqualsAndHashCode(of = {"arenaUniqueID"})
public class MatchResult {

    private long arenaUniqueID;
    private Map<String, Map<String, Object>> personal;
    private Map<MatchInternalVehiclePlayerId, List<MatchVehicleDetail>> vehicles;
    private Map<PlayerId, MatchAvatar> avatars;
    private Map<PlayerId, MatchPlayer> players;
    private MatchCommon common;

    @JsonSetter(value = "vehicles")
    public void setVehiclesData(Map<String, List<MatchVehicleDetail>> vehiclesData) {
        vehicles = dataMapper(vehiclesData, entry -> new MatchInternalVehiclePlayerId(entry.getKey()));
    }

    @JsonGetter(value = "vehicles")
    public Map<String, List<MatchVehicleDetail>> getVehiclesData() {
        return dataMapper(vehicles, entry -> entry.getKey().getId());
    }

    @JsonSetter(value = "avatars")
    public void setAvatarsData(Map<String, MatchAvatar> avatarsData) {
        avatars = dataMapper(avatarsData, entry -> new PlayerId(entry.getKey()));
    }

    @JsonGetter(value = "avatars")
    public Map<String, MatchAvatar> getAvatarsData() {
        return  dataMapper(avatars, entry -> entry.getKey().getId());
    }

    @JsonSetter(value = "players")
    public void setPlayersData(Map<String, MatchPlayer> matchPlayerMap) {
        players = dataMapper(matchPlayerMap, entry -> new PlayerId(entry.getKey()));
    }

    @JsonGetter(value = "players")
    public Map<String, MatchPlayer> getPlayersData() {
        return  dataMapper(players, entry -> entry.getKey().getId());
    }

    private <T, F, S> Map<S, F> dataMapper(Map<T, F> data, Function<Map.Entry<T, F>, S> keyMapper) {
        if (data != null) {
            return data.entrySet()
                    .stream()
                    .collect(toMap(keyMapper, Map.Entry::getValue));
        }
        return null;
    }

}
