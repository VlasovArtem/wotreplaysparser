package org.avlasov.parser.replay.entity.match;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class MatchVehicleDetail {

    private int spotted;
    private Map<String, Object> extPublic;
    private int damageAssistedTrack;
    private int damageReceivedFromInvisibles;
    private int directTeamHits;
    private int damageReceived;
    private int piercingsReceived;
    private int sniperDamageDealt;
    private int soloFlagCapture;
    private int damageAssistedRadio;
    private int mileage;
    private double stunDuration;
    private int piercings;
    private int damageBlockedByArmor;
    private int xp;
    private int droppedCapturePoints;
    @JsonProperty("xp/other")
    private int otherXp;
    private int index;
    private int directHitsReceived;
    private int killerID;
    private int explosionHitsReceived;
    private int achievementXP;
    private int deathReason;
    private int capturePoints;
    private int maxHealth;
    private Object damageEventList;
    private int health;
    private boolean stopRespawn;
    private int achievementCredits;
    private Integer[] achievements;
    @JsonProperty("xp/assist")
    private int assistXp;
    private int shots;
    private int kills;
    private int deathCount;
    private int flagCapture;
    private int damaged;
    private int tdamageDealt;
    private int resourceAbsorbed;
    private int credits;
    private long accountDBID;
    private int lifeTime;
    private int noDamageDirectHitsReceived;
    private int stunned;
    private int equipmentDamageDealt;
    private boolean isTeamKiller;
    private int typeCompDescr;
    private Object capturingBase;
    private int damageAssistedStun;
    private int rolloutsCount;
    private int tkills;
    private int potentialDamageReceived;
    private int damageDealt;
    private int damageAssistedSmoke;
    private Integer[] flagActions;
    private int winPoints;
    private int explosionHits;
    private int team;
    @JsonProperty("xp/attack")
    private int attackXp;
    private Object tdestroyedModules;
    private int stunNum;
    private int damageAssistedInspire;
    private int achievementFreeXP;
    private int directHits;

}
