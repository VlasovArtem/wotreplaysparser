package org.avlasov.entity.wotreplay;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created By artemvlasov on 02/06/2018
 **/
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter
@EqualsAndHashCode
public class WotReplayVehicle {

    private int vehicleMatchId;
    private int spotted;
    private int damageDealt;
    //Damage dealt from more then 300m (contains in damageDealt)
    private int sniperDamageDealt;
    //Урон по вашим разведданным
    private int damageAssistedRadio;
    //Нанесено оглушений
    private int stunNum;
    //Урон по оглушённым вами целям
    private int damageAssistedStun;
    //??
    private int damageAssistedInspire;
    private int directHits;
    private int piercings;
    private int damageBlockedByArmor;
    private int xp;
    //Matches with vehicleMatchId
    private int killerID;
    private int[] achievements;
    //Millage in meters
    private int mileage;
    private int shots;
    private int kills;
    private int damaged;
    private int credits;
    private int accountDBID;
    private int lifeTime;
    private int noDamageDirectHitsReceived;
    private int stunned;
    private int potentialDamageReceived;
    private int damageReceived;
    private int team;

}
