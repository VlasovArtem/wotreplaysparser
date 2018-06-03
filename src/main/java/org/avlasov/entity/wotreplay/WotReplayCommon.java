package org.avlasov.entity.wotreplay;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

/**
 * Created By artemvlasov on 02/06/2018
 **/
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class WotReplayCommon {

    private int duration;
    private int winnerTeam;

}
