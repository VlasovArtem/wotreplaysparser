package org.avlasov.entity.wotreplay;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created By artemvlasov on 02/06/2018
 **/
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class WotReplayPlayer {

    //This field is matches with WotReplayVehicle accountDBID
    private int playerId;
    private String name;
    //Players with the same prebattleID will be in
    private int prebattleID;
    private String clanAbbrev;
    private int team;
    private int clanDBID;

}
