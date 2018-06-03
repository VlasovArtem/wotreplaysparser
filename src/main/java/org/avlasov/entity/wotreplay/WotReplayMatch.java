package org.avlasov.entity.wotreplay;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import org.avlasov.deserializer.WotReplayPlayersDeserializer;
import org.avlasov.deserializer.WotReplayVehiclesDeserializer;

import java.util.List;

/**
 * Created By artemvlasov on 02/06/2018
 **/
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class WotReplayMatch {

    private WotReplayCommon common;
    @JsonDeserialize(using = WotReplayPlayersDeserializer.class)
    private List<WotReplayPlayer> players;
    @JsonDeserialize(using = WotReplayVehiclesDeserializer.class)
    private List<WotReplayVehicle> vehicles;

}
