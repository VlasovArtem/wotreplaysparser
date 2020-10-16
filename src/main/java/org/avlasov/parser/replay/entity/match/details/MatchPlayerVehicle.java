package org.avlasov.parser.replay.entity.match.details;

import lombok.Data;

@Data
public class MatchPlayerVehicle {

    private Integer[] personalMissionIDs;
    private String vehicleType;
    private boolean isAlive;
    private String name;
    private Object personalMissionInfo;
    private boolean forbidInBattleInvitations;
    private int igrType;
    private String clanAbbrev;
    private Integer[][] ranked;
    private int team;
    private Object events;
    private boolean isTeamKiller;

}
