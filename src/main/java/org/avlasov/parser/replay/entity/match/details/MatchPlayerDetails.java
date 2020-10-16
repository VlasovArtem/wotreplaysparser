package org.avlasov.parser.replay.entity.match.details;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import org.avlasov.parser.replay.entity.match.MatchInternalVehiclePlayerId;

import java.time.LocalDateTime;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Data
public class MatchPlayerDetails {

    private String playerVehicle;
    private String clientVersionFromXml;
    private Map<MatchInternalVehiclePlayerId, MatchPlayerVehicle> vehicles;
    private String clientVersionFromExe;
    private String regionCode;
    private long playerID;
    private String serverName;
    private String mapDisplayName;
    private ServerSettings serverSettings;
    private LocalDateTime dateTime;
    private String mapName;
    private String gameplayID;
    private int battleType;
    private boolean hasMods;
    private XVM xvm;
    private String playerName;

    @JsonSetter(value = "vehicles")
    public void setVehiclesData(Map<String, MatchPlayerVehicle> vehiclesData) {
        if (vehiclesData != null) {
            vehicles = vehiclesData.entrySet()
                    .stream()
                    .collect(toMap(entry -> new MatchInternalVehiclePlayerId(entry.getKey()), Map.Entry::getValue));
        }
    }

    @JsonGetter(value = "vehicles")
    public Map<String, MatchPlayerVehicle> getVehiclesData() {
        if (vehicles != null) {
            return vehicles.entrySet()
                    .stream()
                    .collect(toMap(entry -> entry.getKey().getId(), Map.Entry::getValue));
        }
        return null;
    }

}
