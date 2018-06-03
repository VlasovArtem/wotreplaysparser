package org.avlasov.entity.wotreplay;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.avlasov.deserializer.WotReplayLocalDateTimeDeserializer;

import java.time.LocalDateTime;

/**
 * Created By artemvlasov on 02/06/2018
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter
public class WotReplayOwnerInfo {

    private String clientVersionFromXml;
    private String clientVersionFromExe;
    private String mapDisplayName;
    private boolean hasMods;
    private String regionCode;
    private int playerID;
    private String serverName;
    @JsonDeserialize(using = WotReplayLocalDateTimeDeserializer.class)
    private LocalDateTime dateTime;
    private String mapName;
    private String playerName;
    //TODO Create enum
    private int battleType;
    private String playerVehicle;

}
